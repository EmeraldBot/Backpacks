package me.darqy.backpacks.util;

import net.minecraft.server.v1_4_R1.ItemStack;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_4_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

public final class InventoryUtil {
    
    public static void InventoryToNBT(NBTTagCompound tag, Inventory inventory) {        
        NBTTagList list = new NBTTagList();
        org.bukkit.inventory.ItemStack[] contents = inventory.getContents();
        
        tag.setInt("size", contents.length);

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                ItemStack nms = CraftItemStack.asNMSCopy(contents[i]);
                
                nms.save(nbt);
                nbt.setByte("Slot", (byte) i);
                list.add(nbt);
            }
        }
        
        tag.set("contents", list);
    }
    
    
    public static Inventory InventoryFromNBT(NBTTagCompound tag, String title) {
        final int size = tag.getInt("size");
        final Inventory inventory = Bukkit.createInventory(null, size, title);
        final NBTTagList contents = tag.getList("contents");
        
        for (int i = 0, length = contents.size(); i < length; i++) {
            NBTTagCompound nbt = (NBTTagCompound) contents.get(i);
            byte slot = nbt.getByte("Slot");
            ItemStack is = ItemStack.createStack(nbt);
            
            if (is != null) {
                inventory.setItem(slot, CraftItemStack.asBukkitCopy(is));
            }
        }
        
        return inventory;
    }
    
}
