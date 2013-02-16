package me.darqy.backpacks.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.darqy.backpacks.Backpack;
import me.darqy.backpacks.BackpackManager;
import me.darqy.backpacks.Backpacks;
import me.darqy.backpacks.BackpacksConfig;

public class CmdCreateBackpack implements CommandExecutor {
    
    private Backpacks plugin;
    
    public CmdCreateBackpack(Backpacks plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        boolean named = Permissions.createBackpackNamed(s);
        if (!Permissions.createBackpack(s) && !named) {
            s.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
        
        String backpack = "default";
        if (args.length >= 1 && named) {
            backpack = args[0].toLowerCase();
            if (backpack.length() > 16) {
                s.sendMessage(ChatColor.YELLOW + "Please keep backpack name under 16 characters");
                return true;
            }
        }
        
        String player = s.getName();
        boolean other = Permissions.createBackpackOther(s);
        if (args.length >= 2 && other) {
            player = args[1];
        }
        
        String world = args.length >= 3? args[2] : s instanceof Player? ((Player)s).getWorld().getName() : null;
        if (world == null) {
            s.sendMessage(ChatColor.RED + "Missing world parameter!");
            return true;
        }

        BackpackManager manager = plugin.getManager(world);
        if (manager == null) {
            s.sendMessage(ChatColor.RED + "Sorry, can't do that in this world.");
            return true;
        }

        if (manager.hasBackpack(player, backpack)) {
            s.sendMessage(ChatColor.RED + "That backpack already exists.");
            return true;
        }
        
        if (!Permissions.createBackpackLimitBypass(s)) {
            int cap = BackpacksConfig.getMaximumBackpacks();
            if (cap > 0) {
                int backpacks = manager.getBackpackCount(player);
                int max = Permissions.createBackpackLimit(s, cap);
                
                if (backpacks >= max) {
                    s.sendMessage(ChatColor.RED + "Sorry, you've reached your backpack"
                            + " limit of " + max);
                    return true;
                }
            }
        }
        
        Backpack pack = new Backpack(Bukkit.createInventory(null, 54, "Backpack - " + backpack));
        manager.setBackpack(player, backpack, pack);
        s.sendMessage(ChatColor.YELLOW + "Created the new backpack: \"" + backpack + "\"");
        
        return true;
    }
    
}