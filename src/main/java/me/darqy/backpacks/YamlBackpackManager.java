package me.darqy.backpacks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class YamlBackpackManager implements BackpackManager {
    
    private static final String slot = "slot.";
    
    private final File folder;
    private final HashMap<String, PlayerBackpacks> backpacks = new HashMap<String, PlayerBackpacks>();
    
    public YamlBackpackManager(File file) {
        this.folder = file;
        folder.mkdirs();
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("File must be a directory!");
        }
    }

    @Override
    public void saveBackpacks() throws IOException {
        for (PlayerBackpacks player : backpacks.values()) {
            for (Backpack pack : player.getBackpacks()) {
                saveBackpack(player.getPlayer(), pack);
            }
        }
    }

    @Override
    public void saveBackpack(String player, String backpack) throws IOException {
        Backpack pack = getPlayerBackpacks(player).getBackpack(backpack);
        if (pack != null) {
            saveBackpack(player, pack);
        }
    }
    
    private void saveBackpack(String player, Backpack backpack) throws IOException {
        YamlConfiguration config = new YamlConfiguration();
        int i = 0;
        for (ItemStack item : backpack.getInventory().getContents()) {
            if (item != null) {
                config.set(slot + i, item);
            }
            i++;
        }
        config.save(getBackpackFile(player, backpack.getName()));
    }

    @Override
    public int getBackpackCount(String player) {
        File playerFolder = new File(folder, player);
        int count = 0;
        for (File child : playerFolder.listFiles()) {
            if (child.getName().endsWith(".yml")) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<String> getBackpackList(String player) {
        File playerFolder = new File(folder, player);
        ArrayList<String> list = new ArrayList();
        for (File child : playerFolder.listFiles()) {
            if (child.getName().endsWith(".yml")) {
                int i = child.getName().lastIndexOf('.');
                list.add(child.getName().substring(0, i));
            }
        }
        return list;
    }

    @Override
    public boolean loadBackpack(String player, String backpack) {
        File file = getBackpackFile(player, backpack);
        if (!file.exists()) {
            return false;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        Inventory inv = Bukkit.createInventory(null, Backpack.SIZE, "Backpack - " + backpack);
        for (int i = 0, size = inv.getSize(); i < size; i++) {
            String path = slot + i;
            if (config.contains(path)) {
                ItemStack is = config.getItemStack(path);
                if (is != null) {
                    inv.setItem(i, is);
                }
            }
        }
        
        getPlayerBackpacks(player).setBackpack(backpack, new Backpack(backpack, inv));
        return true;
    }

    @Override
    public void closeBackpacks() {
        for (PlayerBackpacks player : backpacks.values()) {
            for (Backpack pack : player.getBackpacks()) {
                pack.closeAll();
            }
        }
    }

    @Override
    public PlayerBackpacks getPlayerBackpacks(String player) {
        if (!backpacks.containsKey(player)) {
            backpacks.put(player, new PlayerBackpacks(this, player));
        }
        return backpacks.get(player);
    }
    
    @Override
    public void renameBackpack(String player, String oldpack, String newpack) {
        File file = getBackpackFile(player, oldpack);
        if (!file.exists()) {
            file.delete();
        }
        getPlayerBackpacks(player).renameBackpack(oldpack, newpack);
        
        try {
            saveBackpack(player, newpack);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private File getBackpackFile(String player, String backpack) {
        return new File(folder, player + File.separator + backpack + ".yml");
    }
    
}
