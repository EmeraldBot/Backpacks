package me.darqy.backpacks;

import me.darqy.backpacks.util.InventoryUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_4_R1.NBTBase;
import net.minecraft.server.v1_4_R1.NBTTagCompound;

public class BackpackManager {
    
    private Map<String, Map<String, Backpack>> backpacks = new HashMap();
    private Map<String, NBTTagCompound> nbts = new HashMap();
    
    private File directory; // working directory
    
    public BackpackManager(File dir) {
        directory = dir;
        
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("File object given must be a directory!");
        }
    }
    
    public boolean hasBackpack(String player, String backpack) {
        checkPlayer(player);
        return backpacks.get(player).containsKey(backpack);
    }
    
    public Backpack getDefaultBackpack(String player) {
        return getBackpack(player, "default");
    }
    
    public Backpack getBackpack(String player, String backpack) {
        checkPlayer(player);
        Map<String, Backpack> packs = backpacks.get(player);
        
        if (!packs.containsKey(backpack)) {
            loadPack(player, backpack);
        }
        
        return packs.get(backpack);
    }
    
    public void setBackpack(String player, String backpack, Backpack pack) {
        checkPlayer(player);
        saveBackpackToNBT(player, backpack, pack);
        backpacks.get(player).put(backpack, pack);
    }
    
    public void renameBackpack(String player, String backpack, String newname) {
        Backpack pack = getBackpack(player, backpack);
        if (pack == null) {
            return;
        }
        
        pack.rename(newname);
        
        nbts.get(player).remove(backpack);
        backpacks.get(player).remove(backpack);
        setBackpack(player, newname, pack);
    }
    
    public List<String> getBackpackList(String player) {
        checkPlayer(player);
        List<String> list = new ArrayList();
        if (nbts.containsKey(player)) {
            for (Object o : nbts.get(player).c()) {
                if (o instanceof NBTTagCompound) {
                    list.add(((NBTTagCompound) o).getString("Name"));
                }
            }
        }
        return list;
    }
    
    /**
     * How many backpacks does the player have
     */
    public int getBackpackCount(String player) {
        if (nbts.containsKey(player)) {
            return nbts.get(player).c().size();
        }
        return 0;
    }
    
    /**
     * Saves all backpacks that have been loaded
     */
    public void saveBackpacks() throws IOException {
        for (String player : backpacks.keySet()) {
            checkPlayer(player);
            Map<String, Backpack> playerpacks = backpacks.get(player);
            
            for (String backpack : playerpacks.keySet()) {
                Backpack pack = playerpacks.get(backpack);
                
                if (pack == null) {
                    continue;
                }
                
                saveBackpackToNBT(player, backpack, pack);
            }
            
            DataOutputStream dataout =
                    new DataOutputStream(
                        new FileOutputStream(
                            new File(directory, player + ".dat")));
            NBTBase.a(nbts.get(player), dataout);
            dataout.close();
        }
    }
    
    public void closeBackpacks() {
        for (String player : backpacks.keySet()) {
            Map<String, Backpack> playerpacks = backpacks.get(player);
            
            for (String backpack : playerpacks.keySet()) {
                Backpack pack = playerpacks.get(backpack);
                if (pack == null) {
                    continue;
                }
                
                pack.closeAll();
            }
        }
    }
    
    private void saveBackpackToNBT(String player, String backpack, Backpack pack) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("Name", backpack);
        InventoryUtil.invToNbt(nbt, pack.getInventory());
        nbts.get(player).setCompound(backpack, nbt);
    }
    
    private void loadNBT(String player) {
        File data = new File(directory, player + ".dat");
        if (data.exists()) {
            try {
                DataInputStream datain = new DataInputStream(new FileInputStream(data));
                nbts.put(player, (NBTTagCompound) NBTBase.b(datain));
                datain.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            nbts.put(player, new NBTTagCompound(player));
        }
    }
    
    private void loadPack(String player, String backpack) {
        NBTTagCompound tag = nbts.get(player);
        if (tag == null || !tag.hasKey(backpack)) {
            return;
        }
        
        backpacks.get(player).put(backpack, new Backpack(tag.getCompound(backpack), backpack));
    }
    
    private void checkPlayer(String player) {
        if (!nbts.containsKey(player)) {
            loadNBT(player);
        }
        
        if (!backpacks.containsKey(player)) {
            backpacks.put(player, new HashMap<String, Backpack>());
        }
    }
    
}
