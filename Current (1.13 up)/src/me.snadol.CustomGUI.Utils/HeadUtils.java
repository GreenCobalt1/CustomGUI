package me.snadol.CustomGUI.Utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Base64;
import java.util.UUID;
import me.snadol.CustomGUI.Reflections;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum HeadUtils {
  ARROW_LEFT("MHF_ArrowLeft"),
  ARROW_RIGHT("MHF_ArrowRight"),
  ARROW_UP("MHF_ArrowUp"),
  ARROW_DOWN("MHF_ArrowDown"),
  QUESTION("MHF_Question"),
  EXCLAMATION("MHF_Exclamation"),
  CAMERA("FHG_Cam"),
  ZOMBIE_PIGMAN("MHF_PigZombie"),
  PIG("MHF_Pig"),
  SHEEP("MHF_Sheep"),
  BLAZE("MHF_Blaze"),
  CHICKEN("MHF_Chicken"),
  COW("MHF_Cow"),
  SLIME("MHF_Slime"),
  SPIDER("MHF_Spider"),
  SQUID("MHF_Squid"),
  VILLAGER("MHF_Villager"),
  OCELOT("MHF_Ocelot"),
  HEROBRINE("MHF_Herobrine"),
  LAVA_SLIME("MHF_LavaSlime"),
  MOOSHROOM("MHF_MushroomCow"),
  GOLEM("MHF_Golem"),
  GHAST("MHF_Ghast"),
  ENDERMAN("MHF_Enderman"),
  CAVE_SPIDER("MHF_CaveSpider"),
  CACTUS("MHF_Cactus"),
  CAKE("MHF_Cake"),
  CHEST("MHF_Chest"),
  MELON("MHF_Melon"),
  LOG("MHF_OakLog"),
  PUMPKIN("MHF_Pumpkin"),
  TNT("MHF_TNT"),
  DYNAMITE("MHF_TNT2");
  
  @SuppressWarnings("unused")
private String id;
  
  HeadUtils(String id) {
    this.id = id;
  }
  
  public static String getMojangURL(String url) {
    byte[] encodedData = Base64.getDecoder().decode(url);
    String decode = (new String(encodedData)).replace("{\"textures\":{\"SKIN\":{\"url\":\"", "").replace("\"}}}", "");
    return decode;
  }
  
  public static ItemStack getCustomSkull(String url) {
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    PropertyMap propertyMap = profile.getProperties();
    if (propertyMap == null)
      throw new IllegalStateException("Profile doesn't contain a property map"); 
    byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", new Object[] { url }).getBytes());
    propertyMap.put("textures", new Property("textures", new String(encodedData)));
    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
    ItemMeta headMeta = head.getItemMeta();
    Class<?> headMetaClass = headMeta.getClass();
    Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
    head.setItemMeta(headMeta);
    return head;
  }
}
