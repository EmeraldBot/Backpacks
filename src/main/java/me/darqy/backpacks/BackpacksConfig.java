package me.darqy.backpacks;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BackpacksConfig {
    
    private static File file;
    private static FileConfiguration config;
    
    private static long saveInterval = 6000;
    private static boolean configuredOnly = true;
    private static int maximumBackpacks = 8;
    private static String backend = "nbt";
    private static String convert = "false";
    
    public static void load(File config_file) {
        file = config_file;
        config = YamlConfiguration.loadConfiguration(file);
        
        saveInterval = config.getLong("save-interval");
        configuredOnly = config.getBoolean("configured-world-only");
        maximumBackpacks = config.getInt("maximum-backpacks-per-group");
        backend = config.getString("backend");
        convert = config.getString("convert");
    }
    
    
    /**
     * The interval, in ticks (1/20 second), between backpack saves to disk
     */
    public static long getSaveInterval() {
        return saveInterval;
    }
    
    
    /**
     * If true, a player will not be able to use backpack commands
     * if the world isn't properly configured in groups.yml
     */
    public static boolean getConfiguredWorldsOnly() {
        return configuredOnly;
    }
    
    /**
     * The limit of backpacks a player may have per configured group
     * of worlds
     */
    public static int getMaximumBackpacks() {
        return maximumBackpacks;
    }
    
    /**
     * The backend used to save backpacks
     */
    public static String getBackend() {
        return backend;
    }
    
    /**
     * Which backend to convert from
     */
    public static String getConverter() {
        return convert;
    }
    
    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static FileConfiguration getConfig() {
        return config;
    }
    
}
