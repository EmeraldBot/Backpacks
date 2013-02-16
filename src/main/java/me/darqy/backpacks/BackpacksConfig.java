package me.darqy.backpacks;

import org.bukkit.configuration.Configuration;

public class BackpacksConfig {
    
    private static long saveInterval = 6000;
    private static boolean configuredOnly = true;
    
    private static int maximumBackpacks = 8;
    
    public BackpacksConfig(Configuration config) {
        saveInterval = config.getLong("save-interval");
        configuredOnly = config.getBoolean("configured-world-only");
        maximumBackpacks = config.getInt("maximum-backpacks-per-group");
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
    
}
