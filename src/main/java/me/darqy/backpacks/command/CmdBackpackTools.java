package me.darqy.backpacks.command;

import java.util.HashMap;
import java.util.Map;
import me.darqy.backpacks.Backpack;
import me.darqy.backpacks.BackpackManager;
import me.darqy.backpacks.Backpacks;
import me.darqy.backpacks.util.InventoryUtil;
import me.darqy.backpacks.util.NMSUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CmdBackpackTools implements CommandExecutor  {
    
    private Backpacks plugin;
    private MagnetListener magnet = this.new MagnetListener();
    
    private static String[] TOOLS = new String[]{"magnet", "empty", "rename", "chest"};
    
    public CmdBackpackTools(Backpacks instance) {
        this.plugin = instance;
        
        plugin.getServer().getPluginManager().registerEvents(magnet, plugin);
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(ChatColor.RED + "This command is only available to players");
            return true;
        }
        
        final Player p = (Player) s;
        final String player = p.getName();
                        
        if (args.length < 1) {
            handleHelp(p, null, l);
            return true;
        }
        
        if ("help".equals(args[0].toLowerCase())) {
            handleHelp(p, args.length >= 2? args[1].toLowerCase(): null, l);
            return true;
        }

        final String action = getTool(args[0]);
        if (action == null) {
            handleHelp(p, args[0], l);
            return true;
        }
        
        if (!p.hasPermission("backpack.tool." + action)) {
            s.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
        
        BackpackManager manager = plugin.getManager(p.getWorld());
        if (manager == null) {
            s.sendMessage(ChatColor.RED + "Sorry, can't do that in this world.");
            return true;
        }

        String backpack = "default";
        for (String arg : args) {
            if (arg.startsWith("p:") || arg.startsWith("P:")) {
                backpack = arg.split(":")[1].toLowerCase();
            }
        }

        Backpack pack = manager.getBackpack(player, backpack);
        if (pack == null) {
            s.sendMessage(ChatColor.RED + "You don't have that backpack.");
            return true;
        }
        
        if ("magnet".equals(action)) {
            handleMagnet(p, pack, backpack);
        } else if ("chest".equals(action)) {
            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "Not enough arguments.");
                p.sendMessage(ChatColor.RED + c.getUsage().replace("<command>", l)
                        .concat(" [put|take] [item|*]"));
                return true;
            }
            String method = args.length >= 3? args[2] : args[1];
            String item = args.length >= 4? args[3] : args[2];
            handleChestTransfer(p, pack, method, item);
        } else if ("rename".equals(action)) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Not enough arguments.");
                p.sendMessage(ChatColor.RED + c.getUsage().replace("<command>", l)
                        .concat(" [newname]"));
                return true;
            }
            String newname = args.length >= 3? args[2] : args[1];
            handleRename(p, manager, backpack, newname.toLowerCase());
        } else if ("empty".equals(action)) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Not enough arguments.");
                p.sendMessage(ChatColor.RED + c.getUsage().replace("<command>", l));
                return true;
            }
            handleEmpty(p, pack, backpack);
        }

        return true;
    }
    
    private void handleMagnet(Player p, Backpack pack, String backpack) {
        if (!magnet.magnetEnabled(p.getName())) {
            magnet.enableMagnet(p.getName(), pack);
            p.sendMessage(ChatColor.YELLOW + "Enabled magnet mode on your "
                    + "\"" + backpack + "\" backpack.");
            p.sendMessage(ChatColor.YELLOW + "Do this command again to disable it.");
        } else {
            magnet.disableMagnet(p.getName());
            p.sendMessage(ChatColor.YELLOW + "Magnet mode disabled.");
        }
    }
    
    private void handleChestTransfer(Player p, Backpack pack, String action, String item) {
        Block target = p.getTargetBlock(null, 5);
        if (!(target.getState() instanceof Chest)) {
            p.sendMessage(ChatColor.RED + "You must be looking at a chest to do that");
            return;
        }
        Chest chest = (Chest) target.getState();

        //todo: Deadbolt/lockette hooks
        Inventory from, to;
        if (action.equalsIgnoreCase("put")) {
            to = chest.getInventory();
            from = pack.getInventory();
        } else if (action.equalsIgnoreCase("take")) {
            to = pack.getInventory();
            from = chest.getInventory();
        } else {
            p.sendMessage(ChatColor.RED + "Error: " + action + ". Use \"put\" or \"take\"");
            return;
        }

        boolean success = InventoryUtil.transferItems(from, to, item);
        if (success) {
            p.sendMessage(ChatColor.YELLOW + "Items transfered!");
        } else {
            p.sendMessage(ChatColor.RED + "Transfer failed. Invalid item?");
        }
    }
    
    private void handleRename(Player p, BackpackManager mngr, String oldname, String newname) {
        if (mngr.hasBackpack(p.getName(), newname)) {
            p.sendMessage(ChatColor.RED + "The backpack you're trying to rename this to already exists.");
            return;
        }
        
        mngr.renameBackpack(p.getName(), oldname, newname);
        p.sendMessage(ChatColor.YELLOW + "Your \"" + oldname + "\" backpack is renamed to: \"" + newname + "\"");
    }
    
    private void handleEmpty(Player p, Backpack pack, String backpack) {
        pack.getInventory().clear();
        p.sendMessage(ChatColor.YELLOW + "Your \"" + backpack + "\" backpack was emptied!");
    }
    
    public void handleHelp(Player p, String action, String l) {
        if (action == null) {
            p.sendMessage(ChatColor.YELLOW + "Unknown action. Available: ");
            for (String tool : TOOLS) {
                if (p.hasPermission("backpack.tool." + tool)) {
                    p.sendMessage("- " + ChatColor.AQUA + tool);
                }
            }
            p.sendMessage(ChatColor.YELLOW + "Do \"/" + l + " help <tool-name>\" for"
                    + " information and usage");
        } else if ("magnet".equals(action)) {
            sendHelpText(p, "Toggling magnet mode on a backpack allows it to"
                    + " collect the items you pickup from the ground, instead of"
                    + " those items being collected into your normal inventory.");
            sendHelpText(p, "---");
            sendHelpText(p, "You must have the room for an item in your normal"
                    + " inventory in order for the item to be collected into"
                    + " your backpack.");
            sendHelpText(p, "When your backpack is full, you will be notified and"
                    + " magnet mode will be automatically disabled.");
            sendHelpText(p, "---");
            sendHelpText(p, "Example usage:");
            sendHelpText(p, " - /" + l +" magnet - Enables magnet on your default backpack");
            sendHelpText(p, " - /" + l +" magnet p:collecter - Enables magnet on your \"collector\""
                    + " backpack");
            sendHelpText(p, " - /" + l +" magnet (after enabled) - disable magnet mode");
        } else if ("chest".equals(action)) {
            sendHelpText(p, " Safely move items from your backpack into a chest, or"
                    + " from a chest into your backpack.");
            sendHelpText(p, "---");
            sendHelpText(p, " You must be looking at the chest you wish to transfer"
                    + " items with.");
            sendHelpText(p, "---");
            sendHelpText(p, "Example usage:");
            sendHelpText(p, " - /" + l +" chest put stone - Move as much stone as possible"
                    + " from your \"default\" backpack into the chest");
            sendHelpText(p, " - /" + l +" chest p:random take * - Moves as many items from the chest"
                    + " into your \"random\" backpack as possible");
        } else if ("rename".equals(action)) {
            sendHelpText(p, " Renames a backpack");
            sendHelpText(p, "---");
            sendHelpText(p, "Example usage:");
            sendHelpText(p, " - /" + l +" rename p:apack other - Rename your \"apack\" backpack to \"other\"");
            sendHelpText(p, " - /" + l +" rename apack - Rename your \"default\" backpack to \"apack\"");
        } else if ("empty".equals(action)) {
            sendHelpText(p, " Empties a backpack");
            sendHelpText(p, "---");
            sendHelpText(p, ChatColor.GOLD + "WARNING: this operation cannot be undone!!"
                    + " You will lose the items in the pack forever.");
            sendHelpText(p, "---");
            sendHelpText(p, "Example usage:");
            sendHelpText(p, " - /" + l +" empty p:apack - Empty your \"apack\" backpack");
        } else {
            p.sendMessage(ChatColor.YELLOW + "Unknown tool: " + action + ". Available:");
            for (String tool : TOOLS) {
                if (p.hasPermission("backpack.tool." + tool)) {
                    p.sendMessage("- " + ChatColor.AQUA + tool);
                }
            }
            p.sendMessage(ChatColor.YELLOW + "Do \"/" + l + " help <tool-name>\" for"
                    + " information and usage");
        }
    }
    
    private void sendHelpText(Player p, String message) {
        p.sendMessage(ChatColor.YELLOW + message);
    }
    
    private String getTool(String filter) {
        for (String tool : TOOLS) {
            if (tool.equalsIgnoreCase(filter)) {
                return tool;
            }
        }
        return null;
    }

    private class MagnetListener implements Listener {
        
        private Map<String, Backpack> magnets = new HashMap();
        
        private static final String backpackFull = 
                "Your backpack is full! Disabled magnet mode.";
        
        public void enableMagnet(String player, Backpack pack) {
            magnets.put(player, pack);
        }
        
        public void disableMagnet(String player) {
            magnets.remove(player);
        }
        
        public boolean magnetEnabled(String player) {
            return magnets.containsKey(player);
        }
        
        @EventHandler(ignoreCancelled = true)
        public void randomPop(PlayerPickupItemEvent event) {
           final Player p = event.getPlayer();
            final Item item = event.getItem();
            final ItemStack itemstack = item.getItemStack();
            final String player = p.getName();
            
            if (magnetEnabled(player)) {
                final Backpack pack = magnets.get(player);
                int left = pack.pickup(itemstack);
                
                if (left > 0) {
                    p.sendMessage(ChatColor.RED + backpackFull);
                    disableMagnet(player);
                    
                    itemstack.setAmount(left);
                    item.setItemStack(itemstack);
                } else {
                    NMSUtil.simulateItemPickup(p, item);
                    item.remove();
                }
                
                event.setCancelled(true);
            }
        }
        
    }
    
}
