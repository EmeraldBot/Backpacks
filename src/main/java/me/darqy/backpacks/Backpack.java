package me.darqy.backpacks;

import me.darqy.backpacks.util.InventoryUtil;
import me.darqy.backpacks.util.SnooperApi;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class Backpack {
    
    private Inventory inventory;
    
    public Backpack(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public Backpack(NBTTagCompound nbt, String name) {
        inventory = InventoryUtil.InventoryFromNBT(nbt, name);
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public InventoryView open(Player viewer) {
        return viewer.openInventory(inventory);
    }
    
    public InventoryView inspect(Player viewer, boolean edit) {
        InventoryView view = open(viewer);
        if (!edit) {
            SnooperApi.registerSnooper(viewer,
                    SnooperApi.constraintHalf(SnooperApi.InvSection.TOP, view));
        }
        return view;
    }
    
}
