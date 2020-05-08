package me.snadol.CustomGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import me.snadol.CustomGUI.Utils.HeadUtils;
import me.snadol.CustomGUI.Utils.Metrics;
import me.snadol.CustomGUI.Utils.SpigotUpdater;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main extends JavaPlugin implements Listener {
    private URL url;

    String argument = "null";

    String argumentip = " ";

    String consoleprefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("MessagesPrefix")) + " ");

    @SuppressWarnings("unused")
    public void onEnable() {
        Metrics metrics = new Metrics((Plugin) this, 0);
        String console = "[CustomGUI] ";
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + console + "Enabled!");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + console + "Found PlaceholderAPI! Placeholders will work.");
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + console + "Could not find PlaceholderAPI! Placeholders will not work.");
        }
        loadConfiguration();
        getServer().getPluginManager().registerEvents(this, (Plugin) this);
        try {
            SpigotUpdater updater = new SpigotUpdater(this, 58440);
            if (updater.checkForUpdates()) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + console + "Update Available!");
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + console + "Up to date!");
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + console + "Could not check for updates!");
        }
    }

    public void onDisable() {
        String console = "[CustomGUI] ";
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + console + "Disabled!");
    }

    public void loadConfiguration() {
        getConfig().options().header("CustomGUI " + getDescription().getVersion() + " config file." +
            "\nPlugin by snadol, https://www.spigotmc.org/members/snadol.528915/\n" +
            "Always try to use single quotes around a config value if it doesn't work before reporting!\n" +
            "==========================================");
        String messageprefix = "MessagesPrefix";
        getConfig().addDefault(messageprefix, "[CustomGUI]");
        String noreload = "Messages.NoPermissionMessages.Command";
        getConfig().addDefault(noreload, "You do not have permission to use the commands!");
        String noopen = "Messages.NoPermissionMessages.Open";
        getConfig().addDefault(noopen, "You do not have permission to open the GUI!");
        String reloaded = "Messages.General.Reloaded";
        getConfig().addDefault(reloaded, "Config Reloaded!");
        String unkarg = "Messages.General.UnknownArg";
        getConfig().addDefault(unkarg, "Unknown Argument!");
        String needarg = "Messages.General.NeedArg";
        getConfig().addDefault(needarg, "This command requires an argument!");
        String needreq = "Messages.General.NoMeetRequirements";
        getConfig().addDefault(needreq, "You do not meet the requirements to use this item!");
        List < String > opencommand = new ArrayList < > ();
        opencommand.add("minerals");
        getConfig().addDefault("Menus.1.OpenCommand", opencommand);
        String size = "Menus.1.Size";
        getConfig().addDefault(size, Integer.valueOf(9));
        String close = "Menus.1.CloseAfterClick";
        getConfig().addDefault(close, Boolean.valueOf(false));
        String guititle = "Menus.1.GUITitle";
        getConfig().addDefault(guititle, "CustomGUI");
        String panes = "Menus.1.FillWithPanes";
        getConfig().addDefault(panes, Boolean.valueOf(false));
        String os = "Menus.1.OpenSound";
        getConfig().addDefault(os, Boolean.valueOf(false));
        String item1name = "Menus.1.Items.Item1.Name";
        String item1lore = "Menus.1.Items.Item1.Lore";
        String item1material = "Menus.1.Items.Item1.Material";
        String item1data = "Menus.1.Items.Item1.Data";
        String item1amount = "Menus.1.Items.Item1.Amount";
        String item1command = "Menus.1.Items.Item1.Command";
        String item1slot = "Menus.1.Items.Item1.Slot";
        String item1attributes = "Menus.1.Items.Item1.Attributes";
        getConfig().addDefault(item1name, "Diamond");
        List < String > item1loree = new ArrayList < > ();
        item1loree.add("A Diamond");
        item1loree.add("Ooh, shiny!");
        getConfig().addDefault(item1lore, item1loree);
        getConfig().addDefault(item1material, "DIAMOND");
        getConfig().addDefault(item1data, Integer.valueOf(0));
        getConfig().addDefault(item1amount, Integer.valueOf(1));
        List < String > item1commandd = new ArrayList < > ();
        item1commandd.add("minecraft:give @p diamond 1");
        getConfig().addDefault(item1command, item1commandd);
        getConfig().addDefault(item1slot, Integer.valueOf(0));
        List < String > item1attributess = new ArrayList < > ();
        item1attributess.add("none");
        getConfig().addDefault(item1attributes, item1attributess);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        @SuppressWarnings("unused")
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        for (int ii = 1; ii <= getConfig().getConfigurationSection("Menus").getKeys(false).size(); ii++) {
            try {
                if (player.getOpenInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Menus." + ii + ".GUITitle").replaceAll("<Arg>", this.argument))) && (
                        event.isRightClick() || event.isLeftClick() || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getClick().isKeyboardClick())) {
                    event.setCancelled(true);
                    for (int i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + ".Items").getKeys(false).size(); i++) {
                        if (event.getSlot() == getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Slot"))
                            if (player.hasPermission("customgui." + ii + "." + i + ".use") || player.hasPermission("customgui." + ii + ".*.use")) {
                                if (getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Command").size() == 1) {
                                    String command = getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Command").replaceAll("<Arg>", this.argument).replaceAll("<Username>", player.getName()).replaceAll("\\[", "").replaceAll("\\]", "");
                                    if (command.startsWith("<console>")) {
                                        if (command.contains("{requirement:")) {
                                            String parse1 = PlaceholderAPI.setPlaceholders(player, command.replaceAll("<console> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                            String splitter = "split";
                                            if (parse1.contains(">")) {
                                                splitter = " > ";
                                                String[] parse2 = parse1.split(splitter);
                                                if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                    player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                    return;
                                                }
                                            }
                                            if (parse1.contains("=")) {
                                                splitter = " = ";
                                                String[] parse2 = parse1.split(splitter);
                                                if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                    player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                    return;
                                                }
                                            }
                                            if (parse1.contains("<")) {
                                                splitter = " < ";
                                                String[] parse2 = parse1.split(splitter);
                                                if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                    player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                    return;
                                                }
                                            }
                                            Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), command.split("} ")[1]);
                                        } else {
                                            Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), command.replaceAll("<console> ", ""));
                                        }
                                        if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                            player.closeInventory();
                                    } else if (command.startsWith("<GUI>")) {
                                        player.closeInventory();
                                        if (command.contains("{requirement:")) {
                                            String parse1 = PlaceholderAPI.setPlaceholders(player, command.replaceAll("<GUI> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                            String splitter = "split";
                                            if (parse1.contains(">")) {
                                                splitter = " > ";
                                                String[] parse2 = parse1.split(splitter);
                                                if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                    player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                    return;
                                                }
                                            }
                                            if (parse1.contains("=")) {
                                                splitter = " = ";
                                                String[] parse2 = parse1.split(splitter);
                                                if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                    player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                    return;
                                                }
                                            }
                                            if (parse1.contains("<")) {
                                                splitter = " < ";
                                                String[] parse2 = parse1.split(splitter);
                                                if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                    player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                    return;
                                                }
                                            }
                                            player.chat("/" + command.split("} ")[1]);
                                        } else {
                                            player.chat("/" + command.replaceAll("<GUI> ", ""));
                                        }
                                    } else {
                                        if (command.startsWith("<sound>")) {
                                            if (command.contains(" {requirement: ")) {
                                                String parse1 = PlaceholderAPI.setPlaceholders(player, command.replaceAll("<sound> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                                String splitter = "split";
                                                if (parse1.contains(">")) {
                                                    splitter = " > ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("=")) {
                                                    splitter = " = ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("<")) {
                                                    splitter = " < ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                player.playSound(player.getLocation(), Sound.valueOf(command.split("} ")[1]), 1.0F, 1.0F);
                                                if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                    player.closeInventory();
                                                return;
                                            }
                                            player.playSound(player.getLocation(), Sound.valueOf(command.replaceAll("<sound> ", "")), 1.0F, 1.0F);
                                            if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                player.closeInventory();
                                            return;
                                        }
                                        if (command.startsWith("<message> ")) {
                                            if (command.contains("{requirement:")) {
                                                String parse1 = PlaceholderAPI.setPlaceholders(player, command.replaceAll("<message> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                                String splitter = "split";
                                                if (parse1.contains(">")) {
                                                    splitter = " > ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(ChatColor.RED + this.consoleprefix + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("=")) {
                                                    splitter = " = ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(ChatColor.RED + this.consoleprefix + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("<")) {
                                                    splitter = " < ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(ChatColor.RED + this.consoleprefix + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                                    player.sendMessage(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', command.split("} ")[1])));
                                                } else {
                                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', command.replaceFirst("<message> ", "")));
                                                }
                                            } else if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                                player.sendMessage(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', command.replaceFirst("<message> ", ""))));
                                            } else {
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', command.replaceFirst("<message> ", "")));
                                            }
                                            if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                player.closeInventory();
                                        } else {
                                            Bukkit.dispatchCommand((CommandSender) player, command);
                                            if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                player.closeInventory();
                                        }
                                    }
                                } else {
                                    event.setCancelled(true);
                                    for (int iii = 0; iii < getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Command").size(); iii++) {
                                        String commandsz = ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Command").get(iii)).replaceAll("<Arg>", this.argument).replaceAll("<Username>", player.getName());
                                        if (commandsz.startsWith("<console>")) {
                                            if (commandsz.contains("{requirement:")) {
                                                String parse1 = PlaceholderAPI.setPlaceholders(player, commandsz.replaceAll("<console> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                                String splitter = "split";
                                                if (parse1.contains(">")) {
                                                    splitter = " > ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("=")) {
                                                    splitter = " = ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("<")) {
                                                    splitter = " < ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), commandsz.split("} ")[1]);
                                            } else {
                                                Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), commandsz.replaceAll("<console> ", ""));
                                            }
                                            if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                player.closeInventory();
                                        } else if (commandsz.startsWith("<GUI>")) {
                                            player.closeInventory();
                                            if (commandsz.contains("{requirement:")) {
                                                String parse1 = PlaceholderAPI.setPlaceholders(player, commandsz.replaceAll("<GUI> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                                String splitter = "split";
                                                if (parse1.contains(">")) {
                                                    splitter = " > ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("=")) {
                                                    splitter = " = ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                if (parse1.contains("<")) {
                                                    splitter = " < ";
                                                    String[] parse2 = parse1.split(splitter);
                                                    if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                        player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                        return;
                                                    }
                                                }
                                                player.chat("/" + commandsz.split("} ")[1]);
                                            } else {
                                                player.chat("/" + commandsz.replaceAll("<GUI> ", ""));
                                            }
                                        } else {
                                            if (commandsz.startsWith("<sound>")) {
                                                if (commandsz.contains(" {requirement: ")) {
                                                    String parse1 = PlaceholderAPI.setPlaceholders(player, commandsz.replaceAll("<sound> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                                    String splitter = "split";
                                                    if (parse1.contains(">")) {
                                                        splitter = " > ";
                                                        String[] parse2 = parse1.split(splitter);
                                                        if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                            player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                            return;
                                                        }
                                                    }
                                                    if (parse1.contains("=")) {
                                                        splitter = " = ";
                                                        String[] parse2 = parse1.split(splitter);
                                                        if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                            player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                            return;
                                                        }
                                                    }
                                                    if (parse1.contains("<")) {
                                                        splitter = " < ";
                                                        String[] parse2 = parse1.split(splitter);
                                                        if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                            player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                            return;
                                                        }
                                                    }
                                                    player.playSound(player.getLocation(), Sound.valueOf(commandsz.split("} ")[1]), 1.0F, 1.0F);
                                                    if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                        player.closeInventory();
                                                    return;
                                                }
                                                player.playSound(player.getLocation(), Sound.valueOf(commandsz.replaceAll("<sound> ", "")), 1.0F, 1.0F);
                                                if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                    player.closeInventory();
                                                return;
                                            }
                                            if (commandsz.startsWith("<message> ")) {
                                                if (commandsz.contains("{requirement:")) {
                                                    String parse1 = PlaceholderAPI.setPlaceholders(player, commandsz.replaceAll("<message> ", "").replaceAll("\\{requirement: ", "").replaceAll("\\}.*", ""));
                                                    String splitter = "split";
                                                    if (parse1.contains(">")) {
                                                        splitter = " > ";
                                                        String[] parse2 = parse1.split(splitter);
                                                        if (Integer.parseInt(parse2[0]) <= Integer.parseInt(parse2[1])) {
                                                            player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                            return;
                                                        }
                                                    }
                                                    if (parse1.contains("=")) {
                                                        splitter = " = ";
                                                        String[] parse2 = parse1.split(splitter);
                                                        if (Integer.parseInt(parse2[0]) != Integer.parseInt(parse2[1])) {
                                                            player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                            return;
                                                        }
                                                    }
                                                    if (parse1.contains("<")) {
                                                        splitter = " < ";
                                                        String[] parse2 = parse1.split(splitter);
                                                        if (Integer.parseInt(parse2[0]) >= Integer.parseInt(parse2[1])) {
                                                            player.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NoMeetRequirements"));
                                                            return;
                                                        }
                                                    }
                                                    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                                        player.sendMessage(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', commandsz.split("} ")[1])));
                                                    } else {
                                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', commandsz.replaceFirst("<message> ", "")));
                                                    }
                                                } else if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                                    player.sendMessage(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', commandsz.replaceFirst("<message> ", ""))));
                                                } else {
                                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', commandsz.replaceFirst("<message> ", "")));
                                                }
                                                if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                    player.closeInventory();
                                            } else {
                                                Bukkit.dispatchCommand((CommandSender) player, commandsz);
                                                if (getConfig().getBoolean("Menus." + ii + ".CloseAfterClick"))
                                                    player.closeInventory();
                                            }
                                        }
                                    }
                                }
                            } else {
                                event.setCancelled(true);
                                if (player.hasPermission("customgui." + ii + "." + i + ".view"))
                                    player.sendMessage(ChatColor.RED + this.consoleprefix + "You don't have permission to use this item!");
                            }
                    }
                }
            } catch (NullPointerException nullPointerException) {}
        }
    }

    @SuppressWarnings("deprecation")
    public void openMenu(Player player, int ii) {
        String panecolor;
        Inventory inv = Bukkit.getServer().createInventory(null, getConfig().getInt("Menus." + ii + ".Size"), ChatColor.translateAlternateColorCodes('&', getConfig().getString("Menus." + ii + ".GUITitle").replaceAll("<Arg>", this.argument)));
        String sound = getConfig().getString("Menus." + ii + ".OpenSound");
        if (sound != "false")
            try {
                player.playSound(player.getLocation(), Sound.valueOf(getConfig().getString("Menus." + ii + ".OpenSound")), 1.0F, 1.0F);
            } catch (IllegalArgumentException eee) {
                System.out.println("[CustomGUI] An error occured while playing the open sound for menu " + ii + ", the OpenSound " + sound + " is invalid!");
            }
        for (int i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + ".Items").getKeys(false).size(); i++) {
            if (player.hasPermission("customgui." + ii + "." + i + ".view") || player.hasPermission("customgui." + ii + ".*.view"))
                if (Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")) == Material.LEATHER_BOOTS || Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")) == Material.LEATHER_LEGGINGS || Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")) == Material.LEATHER_CHESTPLATE || Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")) == Material.LEATHER_HELMET) {
                    ItemStack s = new ItemStack(Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")), getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Amount"), (short) 0);
                    LeatherArmorMeta ss = (LeatherArmorMeta) s.getItemMeta();
                    ArrayList < String > itemLore = new ArrayList < > ();
                    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                        if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Lore").contains("<Arg>") || getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").contains("<Arg>")) {
                            for (int i1 = 1; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                                itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument))).replaceFirst(String.valueOf(this.argument) + " ", ""));
                            ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)).replaceFirst(String.valueOf(this.argument) + " ", "")));
                        } else {
                            for (int i1 = 1; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                                itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1 - 1)).replaceAll("<Arg>", this.argument))));
                            ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument))));
                        }
                    } else {
                        for (int i1 = 1; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument)));
                        ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)));
                    }
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("AQUA"))
                        ss.setColor(Color.AQUA);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("BLACK"))
                        ss.setColor(Color.BLACK);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("BLUE"))
                        ss.setColor(Color.BLUE);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("FUCHSIA"))
                        ss.setColor(Color.FUCHSIA);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("GRAY") || getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("GREY"))
                        ss.setColor(Color.GRAY);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("GREEN"))
                        ss.setColor(Color.GREEN);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("LIME"))
                        ss.setColor(Color.LIME);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("MAROON"))
                        ss.setColor(Color.MAROON);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("NAVY"))
                        ss.setColor(Color.NAVY);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("OLIVE"))
                        ss.setColor(Color.OLIVE);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("ORANGE"))
                        ss.setColor(Color.ORANGE);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("PURPLE"))
                        ss.setColor(Color.PURPLE);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("RED"))
                        ss.setColor(Color.RED);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("SILVER"))
                        ss.setColor(Color.SILVER);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("TEAL"))
                        ss.setColor(Color.TEAL);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("WHITE"))
                        ss.setColor(Color.WHITE);
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").equalsIgnoreCase("YELLOW")) {
                        Bukkit.getLogger().info("YYYY");
                        ss.setColor(Color.YELLOW);
                    }
                    ss.addItemFlags(new ItemFlag[] {
                        ItemFlag.HIDE_ENCHANTS
                    });
                    ss.setLore(itemLore);
                    s.setItemMeta((ItemMeta) ss);
                    if (getConfig().getList("Menus." + ii + ".Items.Item" + i + ".Attributes").contains("glowing"))
                        s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                    inv.setItem(getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Slot"), s);
                } else if (Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")) == Material.PLAYER_HEAD) {
                ItemStack s = new ItemStack(Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")), getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Amount"), (short) 3);
                SkullMeta ss = (SkullMeta) s.getItemMeta();
                ArrayList < String > itemLore = new ArrayList < > ();
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Lore").contains("<Arg>") || getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").contains("<Arg>")) {
                        ss.setOwner(PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument)));
                        ss.setOwner(PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument)));
                        ss.setOwner(PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument)));
                        for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument))));
                        ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)).replaceFirst(String.valueOf(this.argument) + " ", "")));
                    } else {
                        for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument))));
                        ss.setOwner(PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument)));
                        ss.setOwner(PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument)));
                        ss.setOwner(PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument)));
                        ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument))));
                    }
                } else {
                    for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                        itemLore.add(ChatColor.translateAlternateColorCodes('&', ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument)));
                    ss.setOwner(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument));
                    ss.setOwner(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument));
                    ss.setOwner(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data").replaceAll("<Arg>", this.argument));
                    ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)));
                }
                ss.addItemFlags(new ItemFlag[] {
                    ItemFlag.HIDE_ENCHANTS
                });
                ss.setLore(itemLore);
                s.setItemMeta((ItemMeta) ss);
                if (getConfig().getList("Menus." + ii + ".Items.Item" + i + ".Attributes").contains("glowing"))
                    s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv.setItem(getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Slot"), s);
            } else if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material").equals("HEAD_TEXTURE")) {
                String url = HeadUtils.getMojangURL(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Data"));
                ItemStack s = HeadUtils.getCustomSkull(url);
                ItemMeta ss = s.getItemMeta();
                ArrayList < String > itemLore = new ArrayList < > ();
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Lore").contains("<Arg>") || getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").contains("<Arg>")) {
                        for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument))).replaceFirst(String.valueOf(this.argument) + " ", ""));
                        ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)).replaceFirst(String.valueOf(this.argument) + " ", "")));
                    } else {
                        for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1))));
                        ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument))));
                    }
                } else {
                    for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                        itemLore.add(ChatColor.translateAlternateColorCodes('&', ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument)));
                    ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)));
                }
                ss.addItemFlags(new ItemFlag[] {
                    ItemFlag.HIDE_ENCHANTS
                });
                ss.setLore(itemLore);
                s.setItemMeta(ss);
                if (getConfig().getList("Menus." + ii + ".Items.Item" + i + ".Attributes").contains("glowing"))
                    s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv.setItem(getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Slot"), s);
            } else {
                ItemStack s = new ItemStack(Material.matchMaterial(getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Material")), getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Amount"), (short) getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Data"));
                ItemMeta ss = s.getItemMeta();
                ArrayList < String > itemLore = new ArrayList < > ();
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Lore").contains("<Arg>") || getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").contains("<Arg>")) {
                        for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument))).replaceFirst(String.valueOf(this.argument) + " ", ""));
                        ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(this.argument), getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)).replaceFirst(String.valueOf(this.argument) + " ", "")));
                    } else {
                        for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1))));
                        ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument))));
                    }
                } else {
                    for (int i1 = 0; i1 <= getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").size() - 1; i1++)
                        itemLore.add(ChatColor.translateAlternateColorCodes('&', ((String) getConfig().getStringList("Menus." + ii + ".Items.Item" + i + ".Lore").get(i1)).replaceAll("<Arg>", this.argument)));
                    ss.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").replaceAll("<Arg>", this.argument)));
                }
                ss.addItemFlags(new ItemFlag[] {
                    ItemFlag.HIDE_ENCHANTS
                });
                ss.setLore(itemLore);
                s.setItemMeta(ss);
                if (getConfig().getList("Menus." + ii + ".Items.Item" + i + ".Attributes").contains("glowing"))
                    s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv.setItem(getConfig().getInt("Menus." + ii + ".Items.Item" + i + ".Slot"), s);
            }
        }
        String fillwithpanes = getConfig().getString("Menus." + ii + ".FillWithPanes");
        String enabled = fillwithpanes.split(",")[0].trim();
        try {
            panecolor = fillwithpanes.split(",")[1].trim();
        } catch (ArrayIndexOutOfBoundsException err) {
            panecolor = "7";
        }
        if (Boolean.parseBoolean(enabled))
            for (int j = 0; j <= getConfig().getInt("Menus." + ii + ".Size") - 1; j++) {
                if (inv.getItem(j) == null) {
                    ItemStack pane = new ItemStack(Material.matchMaterial(String.valueOf(panecolor) + "_STAINED_GLASS_PANE"), 1);
                    ItemMeta panem = pane.getItemMeta();
                    panem.setDisplayName(" ");
                    pane.setItemMeta(panem);
                    inv.setItem(j, pane);
                }
            }
        player.updateInventory();
        player.openInventory(inv);
        player.updateInventory();
        player.updateInventory();
    }

    @EventHandler
    public void playerCommandChat(PlayerCommandPreprocessEvent event) {
        for (int ii = 1; ii <= getConfig().getConfigurationSection("Menus").getKeys(false).size(); ii++) {
            Boolean arg = null;
            String consoleprefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("MessagesPrefix")) + " ");
            Player player = event.getPlayer();
            if (getConfig().getStringList("Menus." + ii + ".OpenCommand").size() == 1) {
                String command = "/" + getConfig().getList("Menus." + ii + ".OpenCommand").get(0);
                String commandwarg = "/" + getConfig().getList("Menus." + ii + ".OpenCommand").get(0) + " ";
                List < String > arg1 = new ArrayList < > ();
                int i;
                for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + ".Items").getKeys(false).size(); i++) {
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Command").contains("<Arg>"))
                        arg1.add("Yes");
                }
                if (getConfig().getString("Menus." + ii + ".GUITitle").contains("<Arg>"))
                    arg1.add("Yes");
                for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + "." + "Items").getKeys(false).size(); i++) {
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Lore").contains("<Arg>"))
                        arg1.add("Yes");
                }
                for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + "." + "Items").getKeys(false).size(); i++) {
                    if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").contains("<Arg>"))
                        arg1.add("Yes");
                }
                if (arg1.contains("Yes")) {
                    arg = Boolean.valueOf(true);
                } else {
                    arg = Boolean.valueOf(false);
                }
                if (event.getMessage().equals(command)) {
                    if (event.getMessage().equalsIgnoreCase(command) && event.getPlayer() instanceof Player) {
                        event.setCancelled(true);
                        if (player.hasPermission("customgui.open." + ii)) {
                            if (!arg.booleanValue()) {
                                reloadConfig();
                                openMenu(player, ii);
                            } else {
                                event.setCancelled(true);
                                player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NeedArg"));
                            }
                        } else {
                            event.setCancelled(true);
                            player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.NoPermissionMessages.Open"));
                        }
                    }
                } else if (event.getMessage().startsWith(commandwarg)) {
                    if (player.hasPermission("customgui.open." + ii)) {
                        if (arg.booleanValue()) {
                            this.argumentip = event.getMessage().replaceFirst(command, "");
                            this.argument = this.argumentip.replaceFirst(" ", "");
                            reloadConfig();
                            openMenu(player, ii);
                            event.setCancelled(true);
                        } else {
                            event.setCancelled(true);
                            player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.UnknownArg"));
                        }
                    } else {
                        event.setCancelled(true);
                        player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.NoPermissionMessages.Open"));
                    }
                }
            } else {
                for (int iii = 0; iii <= getConfig().getConfigurationSection("Menus").getKeys(false).size(); iii++) {
                    String command = "/" + getConfig().getList("Menus." + ii + ".OpenCommand").get(iii);
                    String commandwarg = "/" + getConfig().getList("Menus." + ii + ".OpenCommand").get(iii) + " ";
                    List < String > arg1 = new ArrayList < > ();
                    int i;
                    for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + ".Items").getKeys(false).size(); i++) {
                        if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Command").contains("<Arg>"))
                            arg1.add("Yes");
                    }
                    if (getConfig().getString("Menus." + ii + ".GUITitle").contains("<Arg>"))
                        arg1.add("Yes");
                    for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + "." + "Items").getKeys(false).size(); i++) {
                        if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Lore").contains("<Arg>"))
                            arg1.add("Yes");
                    }
                    for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + "." + "Items").getKeys(false).size(); i++) {
                        if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").contains("<Arg>"))
                            arg1.add("Yes");
                    }
                    if (arg1.contains("Yes")) {
                        arg = Boolean.valueOf(true);
                    } else {
                        arg = Boolean.valueOf(false);
                    }
                    if (event.getMessage().equals(command)) {
                        if (event.getMessage().equalsIgnoreCase(command) && event.getPlayer() instanceof Player) {
                            event.setCancelled(true);
                            if (player.hasPermission("customgui.open." + ii)) {
                                if (!arg.booleanValue()) {
                                    reloadConfig();
                                    openMenu(player, ii);
                                } else {
                                    event.setCancelled(true);
                                    player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.NeedArg"));
                                }
                            } else {
                                event.setCancelled(true);
                                player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.NoPermissionMessages.Open"));
                            }
                        }
                    } else if (event.getMessage().startsWith(commandwarg)) {
                        if (player.hasPermission("customgui.open." + ii)) {
                            if (arg.booleanValue()) {
                                this.argumentip = event.getMessage().replaceFirst(command, "");
                                this.argument = this.argumentip.replaceFirst(" ", "");
                                reloadConfig();
                                openMenu(player, ii);
                                event.setCancelled(true);
                            } else {
                                event.setCancelled(true);
                                player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.General.UnknownArg"));
                            }
                        } else {
                            event.setCancelled(true);
                            player.sendMessage(String.valueOf(consoleprefix) + ChatColor.RED + getConfig().getString("Messages.NoPermissionMessages.Open"));
                        }
                    }
                }
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        Boolean arg = Boolean.valueOf(false);
        for (int ii = 1; ii <= getConfig().getConfigurationSection("Menus").getKeys(false).size(); ii++) {
            List < String > arg1 = new ArrayList < > ();
            int i;
            for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + ".Items").getKeys(false).size(); i++) {
                if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Command").contains("<Arg>"))
                    arg1.add("Yes");
            }
            if (getConfig().getString("Menus." + ii + ".GUITitle").contains("<Arg>"))
                arg1.add("Yes");
            for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + "." + "Items").getKeys(false).size(); i++) {
                if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Lore").contains("<Arg>"))
                    arg1.add("Yes");
            }
            for (i = 1; i <= getConfig().getConfigurationSection("Menus." + ii + "." + "Items").getKeys(false).size(); i++) {
                if (getConfig().getString("Menus." + ii + ".Items.Item" + i + ".Name").contains("<Arg>"))
                    arg1.add("Yes");
            }
            if (arg1.contains("Yes")) {
                arg = Boolean.valueOf(true);
            } else {
                arg = Boolean.valueOf(false);
            }
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "CustomGUI by snadol");
            sender.sendMessage(ChatColor.GREEN + "Running version " + getDescription().getVersion());
        } else if (args.length == 1) {
            if (sender.hasPermission("customgui.command")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    sender.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.General.Reloaded")));
                } else if (args[0].equalsIgnoreCase("open")) {
                    sender.sendMessage(String.valueOf(this.consoleprefix) + "This command requires valid arguments! /customgui open (Menu #) (Player)");
                } else {
                    sender.sendMessage(String.valueOf(this.consoleprefix) + "This command requires a valid argument! /customgui (reload/open)");
                }
            } else {
                sender.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NoPermissionMessages.Command")));
            }
        } else if (args.length == 2) {
            if (sender.hasPermission("customgui.command")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(String.valueOf(this.consoleprefix) + "Too many arguments!");
                } else if (args[0].equalsIgnoreCase("open")) {
                    reloadConfig();
                    if (isInt(args[1])) {
                        Player player = (Player) sender;
                        openMenu(player, Integer.parseInt(args[1]));
                    } else {
                        sender.sendMessage(String.valueOf(this.consoleprefix) + "The menu ID must be a number!");
                    }
                } else {
                    sender.sendMessage(String.valueOf(this.consoleprefix) + "This command requires a valid argument! /customgui (reload/open)");
                }
            } else {
                sender.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NoPermissionMessages.Command")));
            }
        } else if (args.length == 3) {
            if (sender.hasPermission("customgui.command")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(String.valueOf(this.consoleprefix) + "Too many arguments!");
                } else if (args[0].equalsIgnoreCase("open")) {
                    if (arg.booleanValue()) {
                        sender.sendMessage(String.valueOf(this.consoleprefix) + " This command needs an argument to open the menu!");
                    } else if (isInt(args[1])) {
                        Player subject = Bukkit.getPlayer(args[2]);
                        if (subject != null) {
                            openMenu(subject, Integer.parseInt(args[1]));
                            sender.sendMessage(String.valueOf(this.consoleprefix) + "Menu opened!");
                        } else {
                            sender.sendMessage(String.valueOf(this.consoleprefix) + "That is not a valid player!");
                        }
                    } else {
                        sender.sendMessage(String.valueOf(this.consoleprefix) + "The menu ID must be a number!");
                    }
                } else {
                    sender.sendMessage(String.valueOf(this.consoleprefix) + "This command requires a valid argument! /customgui (reload/open)");
                }
            } else {
                sender.sendMessage(String.valueOf(this.consoleprefix) + ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NoPermissionMessages.Command")));
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("open")) {
                if (arg.booleanValue()) {
                    if (isInt(args[1])) {
                        Player subject = Bukkit.getPlayer(args[2]);
                        if (subject != null) {
                            this.argument = args[3];
                            openMenu(subject, Integer.parseInt(args[1]));
                            sender.sendMessage(String.valueOf(this.consoleprefix) + "Menu opened!");
                        } else {
                            sender.sendMessage(String.valueOf(this.consoleprefix) + "That is not a valid player!");
                        }
                    } else {
                        sender.sendMessage(String.valueOf(this.consoleprefix) + "The menu ID must be a number!");
                    }
                } else {
                    sender.sendMessage(String.valueOf(this.consoleprefix) + "This command did not expect an argument!!");
                }
            } else {
                sender.sendMessage(String.valueOf(this.consoleprefix) + "Too many arguments for this subcommand!");
            }
        } else {
            sender.sendMessage(String.valueOf(this.consoleprefix) + "This command requires a valid argument! /customgui (reload/open)");
        }
        return true;
    }

    public int checkDownloads() {
        Integer downloads = null;
        try {
            this.url = new URL("https://api.spiget.org/v2/resources/58440");
        } catch (MalformedURLException malformedURLException) {}
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) this.url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = "";
            String line = null;
            while ((line = in .readLine()) != null)
                content = String.valueOf(content) + line; in .close();
            JSONObject json = null;
            try {
                json = (JSONObject)(new JSONParser()).parse(content);
            } catch (ParseException parseException) {}
            if (json != null && json.containsKey("downloads"))
                downloads = (Integer) json.get("downloads");
        } catch (IOException e) {
            if (connection != null)
                try {
                    int code = connection.getResponseCode();
                    getLogger().warning("ERROR - Error Code : " + code);
                } catch (IOException iOException) {}
            getLogger().warning("Failed to contact spigot!");
        }
        return downloads;
    }

    public boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }	
}
