package me.darqy.backpacks;

import java.util.HashMap;
import me.darqy.backpacks.util.InventoryUtil;
import me.darqy.backpacks.util.SnooperApi;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class Backpack {
    
    private Inventory inventory;
    
    public Backpack(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public Backpack(NBTTagCompound nbt, String name) {
        inventory = InventoryUtil.invFromNbt(nbt, "Backpack - " + name);
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
    
    public int pickup(ItemStack item) {
        HashMap<Integer, ItemStack> left = inventory.addItem(item);
        return left.isEmpty()? 0 : left.get(0).getAmount();
    }
    
    public void rename(String title) {
        closeAll();
        
        ItemStack[] contents = inventory.getContents();
        inventory = Bukkit.createInventory(null, contents.length, "Backpack - " + title);
        inventory.setContents(contents);
    }
    
    public void closeAll() {
        for (HumanEntity he : inventory.getViewers()) {
            he.closeInventory();
        }
    }
    
}
