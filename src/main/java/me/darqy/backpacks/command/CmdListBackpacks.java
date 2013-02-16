package me.darqy.backpacks.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.darqy.backpacks.BackpackManager;
import me.darqy.backpacks.Backpacks;

public class CmdListBackpacks implements CommandExecutor {

    private Backpacks plugin;

    public CmdListBackpacks(Backpacks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        boolean other = Permissions.listBackpacksOther(s);
        if (!Permissions.listBackpacks(s) && !other) {
            s.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }
        
        String world = args.length >= 1 ? args[0] : s instanceof Player? ((Player)s).getWorld().getName() : null;
        if (world == null) {
            s.sendMessage(ChatColor.RED + "Missing world parameter!");
            return true;
        }
        
        String player = s.getName();
        if (args.length >= 2 && other) {
            player = args[1];
        }

        BackpackManager manager = plugin.getManager(world);
        if (manager == null) {
            s.sendMessage(ChatColor.RED + "Sorry, can't do that in this world.");
            return true;
        }
        
        List<String> packs = manager.getBackpackList(player);
        Collections.sort(packs);
        
        if (packs.isEmpty()) {
            s.sendMessage(ChatColor.YELLOW + "No backpacks found");
            return true;
        }
        
        s.sendMessage(ChatColor.YELLOW + "Backpacks:");
        for (String name : packs) {
           s.sendMessage(ChatColor.YELLOW + "- " + name);
        }
        return true;
    }
}
