package me.darqy.backpacks.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.darqy.backpacks.Backpack;
import me.darqy.backpacks.BackpackManager;
import me.darqy.backpacks.Backpacks;

public class CmdBackpack implements CommandExecutor {
    
    private Backpacks plugin;
    
    public CmdBackpack(Backpacks plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(ChatColor.RED + "This command is only available to players");
            return true;
        }
        
        boolean named = s.hasPermission("backpack.use.named");
        if (!s.hasPermission("backpack.use.default")
                && !named) {
            s.sendMessage(ChatColor.RED + "You don't have permission.");
            return false;
        }
        
        String backpack = "default";
        if (args.length >= 1 && named) {
            backpack = args[0].toLowerCase();
        }

        BackpackManager manager = plugin.getManager(((Player) s).getWorld());
        if (manager == null) {
            s.sendMessage(ChatColor.RED + "Sorry, can not do that in this world");
            return true;
        }
        
        Backpack pack = manager.getBackpack(s.getName(), backpack);
        
        if (pack == null) {
            s.sendMessage(ChatColor.RED + "You don't have that backpack.");
            return true;
        }
        
        final Player player = (Player) s;
        pack.open(player);
        return true;
    }
    
}