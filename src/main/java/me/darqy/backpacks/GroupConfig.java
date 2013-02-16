package me.darqy.backpacks;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

public class GroupConfig {
    
    //          World,  Group
    private Map<String, String> groupMapping;
    
    public GroupConfig(Configuration config) {
        groupMapping = new HashMap();
        ConfigurationSection groups = config.getConfigurationSection("groups");
        
        for (String key : groups.getKeys(false)) {
            for (String world : groups.getStringList(key)) {
                groupMapping.put(world, key);
            }
        }
    }
    
    public String getGroup(String world) {
        if (groupMapping.containsKey(world)) {
            return groupMapping.get(world);
        }
        return "default";
    }
    
    public String getGroup(World world) {
        return getGroup(world.getName());
    }
    
    public boolean configured(String world) {
        return groupMapping.containsKey(world);
    }
    
}
