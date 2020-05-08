package me.snadol.CustomGUI;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;


@SuppressWarnings("rawtypes")
public final class Reflections {
  private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
  
  private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
  
  private static String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
  
  private static Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");
  
  private static String expandVariables(String name) {
    StringBuffer output = new StringBuffer();
    Matcher matcher = MATCH_VARIABLE.matcher(name);
    while (matcher.find()) {
      String replacement, variable = matcher.group(1);
      if ("nms".equalsIgnoreCase(variable)) {
        replacement = NMS_PREFIX;
      } else if ("obc".equalsIgnoreCase(variable)) {
        replacement = OBC_PREFIX;
      } else if ("version".equalsIgnoreCase(variable)) {
        replacement = VERSION;
      } else {
        throw new IllegalArgumentException("Unknown variable: " + variable);
      } 
      if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
        replacement = String.valueOf(replacement) + "."; 
      matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
    } 
    matcher.appendTail(output);
    return output.toString();
  }
  
  private static Class<?> getCanonicalClass(String canonicalName) {
    try {
      return Class.forName(canonicalName);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Cannot find " + canonicalName, e);
    } 
  }
  
  public static Class<?> getClass(String lookupName) {
    return getCanonicalClass(expandVariables(lookupName));
  }
  
  public static ConstructorInvoker getConstructor(String className, Class... params) {
    return getConstructor(getClass(className), params);
  }
  
  public static ConstructorInvoker getConstructor(Class<?> clazz, Class... params) {
    byte b;
    int i;
    Constructor[] arrayOfConstructor;
    for (i = (arrayOfConstructor = (Constructor[])clazz.getDeclaredConstructors()).length, b = 0; b < i; ) {
      final Constructor<?> constructor = arrayOfConstructor[b];
      if (Arrays.equals((Object[])constructor.getParameterTypes(), (Object[])params)) {
        constructor.setAccessible(true);
        return new ConstructorInvoker() {
            public Object invoke(Object... arguments) {
              try {
                return constructor.newInstance(arguments);
              } catch (Exception e) {
                throw new RuntimeException("Cannot invoke constructor " + constructor, e);
              } 
            }
          };
      } 
      b++;
    } 
    throw new IllegalStateException(String.format(
          "Unable to find constructor for %s (%s).", new Object[] { clazz, Arrays.asList(params) }));
  }
  
  public static Class<?> getCraftBukkitClass(String name) {
    return getCanonicalClass(String.valueOf(OBC_PREFIX) + "." + name);
  }
  
  public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
    return getField(target, name, fieldType, 0);
  }
  
  public static <T> FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
    return getField(getClass(className), name, fieldType, 0);
  }
  
  public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
    return getField(target, null, fieldType, index);
  }
  
  public static <T> FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
    return getField(getClass(className), fieldType, index);
  }
  
  private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
    byte b;
    int i;
    Field[] arrayOfField;
    for (i = (arrayOfField = target.getDeclaredFields()).length, b = 0; b < i; ) {
      final Field field = arrayOfField[b];
      if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
        field.setAccessible(true);
        return new FieldAccessor<T>() {
            @SuppressWarnings("unchecked")
			public T get(Object target) {
              try {
                return (T)field.get(target);
              } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access reflection.", e);
              } 
            }
            
            public void set(Object target, Object value) {
              try {
                field.set(target, value);
              } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access reflection.", e);
              } 
            }
            
            public boolean hasField(Object target) {
              return field.getDeclaringClass().isAssignableFrom(target.getClass());
            }
          };
      } 
      b++;
    } 
    if (target.getSuperclass() != null)
      return getField(target.getSuperclass(), name, fieldType, index); 
    throw new IllegalArgumentException("Cannot find field with type " + fieldType);
  }
  
  public static MethodInvoker getMethod(String className, String methodName, Class... params) {
    return getTypedMethod(getClass(className), methodName, null, params);
  }
  
  public static MethodInvoker getMethod(Class<?> clazz, String methodName, Class... params) {
    return getTypedMethod(clazz, methodName, null, params);
  }
  
  public static Method getMethodSimply(Class<?> clazz, String method) {
    byte b;
    int i;
    Method[] arrayOfMethod;
    for (i = (arrayOfMethod = clazz.getMethods()).length, b = 0; b < i; ) {
      Method m = arrayOfMethod[b];
      if (m.getName().equals(method))
        return m; 
      b++;
    } 
    return null;
  }
  
  public static Class<?> getMinecraftClass(String name) {
    return getCanonicalClass(String.valueOf(NMS_PREFIX) + "." + name);
  }
  
  public static MethodInvoker getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, Class... params) {
    byte b;
    int i;
    Method[] arrayOfMethod;
    for (i = (arrayOfMethod = clazz.getDeclaredMethods()).length, b = 0; b < i; ) {
      final Method method = arrayOfMethod[b];
      if (((methodName == null || method.getName().equals(methodName)) && 
        returnType == null) || (method.getReturnType().equals(returnType) && 
        Arrays.equals((Object[])method.getParameterTypes(), (Object[])params))) {
        method.setAccessible(true);
        return new MethodInvoker() {
            public Object invoke(Object target, Object... arguments) {
              try {
                return method.invoke(target, arguments);
              } catch (Exception e) {
                throw new RuntimeException("Cannot invoke method " + method, e);
              } 
            }
          };
      } 
      b++;
    } 
    if (clazz.getSuperclass() != null)
      return getMethod(clazz.getSuperclass(), methodName, params); 
    throw new IllegalStateException(String.format(
          "Unable to find method %s (%s).", new Object[] { methodName, Arrays.asList(params) }));
  }
  
  public static Class<Object> getUntypedClass(String lookupName) {
    @SuppressWarnings({ "unchecked" })
	Class<Object> clazz = (Class)getClass(lookupName);
    return clazz;
  }
  
  public static <T> T newInstance(Class<T> type) {
    try {
      return type.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public static interface ConstructorInvoker {
    Object invoke(Object... param1VarArgs);
  }
  
  public static interface FieldAccessor<T> {
    T get(Object param1Object);
    
    void set(Object param1Object1, Object param1Object2);
    
    boolean hasField(Object param1Object);
  }
  
  public static interface MethodInvoker {
    Object invoke(Object param1Object, Object... param1VarArgs);
  }
}
