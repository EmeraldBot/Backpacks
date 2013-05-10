package me.darqy.backpacks.util;

import net.minecraft.server.v1_5_R3.EntityTracker;
import net.minecraft.server.v1_5_R3.Packet22Collect;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public class NMSUtil {
    
    public static void simulateItemPickup(Player player, Item item) {
        simulateItemPickup(player, item.getEntityId());
    }
    
    public static void simulateItemPickup(Player player, int entity) {
        CraftPlayer p = (CraftPlayer) player;
        EntityTracker tracker = ((CraftWorld) p.getWorld()).getHandle().getTracker();
        tracker.sendPacketToEntity(p.getHandle(),
                new Packet22Collect(entity, p.getEntityId()));
    }
    
}
