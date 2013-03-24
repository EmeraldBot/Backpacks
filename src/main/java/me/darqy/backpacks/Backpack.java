package me.darqy.backpacks;

import java.util.HashMap;
import me.darqy.backpacks.util.SnooperApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class Backpack {
    
    public static final int SIZE = 54;
    
    private String name;
    private Inventory inventory;
    
    public Backpack(String name) {
        this.name = name;
        this.inventory = Bukkit.createInventory(null, SIZE, "Backpack - " + name);
    }
    
    public Backpack(String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
    }
        
    public String getName() {
        return name;
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
    
    public void rename(String name) {
        closeAll();
        this.name = name;
        
        ItemStack[] contents = inventory.getContents();
        inventory = Bukkit.createInventory(null, contents.length, "Backpack - " + name);
        inventory.setContents(contents);
    }
    
    public void closeAll() {
        for (HumanEntity he : inventory.getViewers()) {
            he.closeInventory();
        }
    }
    
}
