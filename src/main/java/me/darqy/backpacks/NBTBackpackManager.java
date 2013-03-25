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
import net.minecraft.server.v1_5_R2.NBTBase;
import net.minecraft.server.v1_5_R2.NBTTagCompound;
import org.bukkit.inventory.Inventory;

public class NBTBackpackManager extends BackpackManager {

    private File folder;
    private Map<String, NBTTagCompound> tags = new HashMap();
    

    public NBTBackpackManager(File dir) {
        folder = dir;
        folder.mkdirs();
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("File must be a directory!");
        }
    }

    @Override
    public void renameBackpack(String player, String oldBackpack, String newBackpack) {
        getNBT(player).remove(oldBackpack);
        getPlayerBackpacks(player).renameBackpack(oldBackpack, newBackpack);
        
        try {
            saveBackpack(player, newBackpack);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<String> getBackpackList(String player) {
        List<String> list = new ArrayList();
        for (Object o : getNBT(player).c()) {
            if (o instanceof NBTTagCompound) {
                list.add(((NBTTagCompound) o).getString("Name"));
            }
        }
        return list;
    }

    @Override
    public int getBackpackCount(String player) {
        return getNBT(player).c().size();
    }
    
    @Override
    public void saveBackpacks() throws IOException {
        for (PlayerBackpacks player : backpacks.values()) {
            for (Backpack pack : player.getBackpacks()) {
                saveBackpack(player.getPlayer(), pack);
            }
            savePlayerFile(player.getPlayer());
        }
    }
    
    @Override
    public void saveBackpack(String player, String backpack) throws IOException {
        Backpack pack = getPlayerBackpacks(player).getBackpack(backpack);
        if (pack != null) {
            saveBackpack(player, pack);
        }
    }
    
    @Override
    public boolean loadBackpack(String player, String backpack) {
        NBTTagCompound tag = getNBT(player);
        if (!tag.hasKey(backpack)) {
            return false;
        }
        Inventory inv = InventoryUtil.invFromNbt((NBTTagCompound) tag.get(backpack), backpack);
        getPlayerBackpacks(player).setBackpack(backpack, new Backpack(backpack, inv));
        return true;
    }
    
    @Override
    public void loadAll() {
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".dat")) {
                String player = file.getName().substring(0, file.getName().length() - 4);
                NBTTagCompound tag = getNBT(player);
                if (tag.isEmpty()) {
                    continue;
                }
                
                for (Object o : getNBT(player).c()) {
                    if (o instanceof NBTTagCompound) {
                        loadBackpack(player, ((NBTTagCompound) o).getString("Name"));
                    }
                }
            }
        }
    }
    
    private void saveBackpack(String player, Backpack backpack) throws IOException {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Name", backpack.getName());
        InventoryUtil.invToNbt(tag, backpack.getInventory());
        getNBT(player).setCompound(backpack.getName(), tag);
    }
    
    private void savePlayerFile(String player) throws IOException {
        File file = getFile(player);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        NBTBase.a(getNBT(player), dos);
        dos.close();
    }

    private NBTTagCompound getNBT(String player) {
        if (!tags.containsKey(player)) {
            File data = getFile(player);
            if (data.exists()) {
                try {
                    DataInputStream din = new DataInputStream(new FileInputStream(data));
                    tags.put(player, (NBTTagCompound) NBTBase.b(din));
                    din.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                tags.put(player, new NBTTagCompound(player));
            }
        }
        return tags.get(player);
    }

    private File getFile(String player) {
        return new File(folder, player + ".dat");
    }

}
