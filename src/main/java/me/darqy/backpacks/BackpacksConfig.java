package me.darqy.backpacks;

import org.bukkit.configuration.Configuration;

public class BackpacksConfig {
    
    private long saveInterval = 6000;
    private boolean configuredOnly = true;
    
    public BackpacksConfig(Configuration config) {
        saveInterval = config.getLong("save-interval");
        configuredOnly = config.getBoolean("configured-world-only");
    }
    
    public long getSaveInterval() {
        return saveInterval;
    }
    
    public boolean getConfiguredWorldsOnly() {
        return configuredOnly;
    }
    
}
