package me.darqy.backpacks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.Validate;

public abstract class BackpackManager {
    
    public Map<String, PlayerBackpacks> backpacks = new HashMap();
    
    /**
     * Saves all loaded backpacks to disk.
     * @throws IOException 
     */
    public abstract void saveBackpacks() throws IOException;
    
    /**
     * Saves a specific backpack. Whether or not the data is then saved to
     * disk is up to the implementation.
     * @param player
     * @param backpack
     * @throws IOException 
     */
    public abstract void saveBackpack(String player, String backpack) throws IOException;
    
    /**
     * Gets the amount of backpacks a player has. (loaded or not)
     * @param player
     * @return amount of backpacks a player has
     */
    public abstract int getBackpackCount(String player);
    
    /**
     * A list of all the backpacks a player has. (loaded or not)
     * @param player
     * @return a List of backpack names
     */
    public abstract List<String> getBackpackList(String player);
    
    /**
     * Loads a backpack from disk
     * @param player
     * @param backpack
     * @return whether or not the backpack storage was available
     */
    public abstract boolean loadBackpack(String player, String backpack);
    
    /**
     * Closes all open inventory screens
     */
    public void closeBackpacks() {
        for (PlayerBackpacks player : backpacks.values()) {
            for (Backpack pack : player.getBackpacks()) {
                pack.closeAll();
            }
        }
    }
    
    /**
     * Gets the PlayerBackpacks object, which holds all loaded backpacks
     * for a player.
     * @param player
     * @return 
     */
    public PlayerBackpacks getPlayerBackpacks(String player) {
        if (!backpacks.containsKey(player)) {
            backpacks.put(player, new PlayerBackpacks(this, player));
        }
        return backpacks.get(player);
    }
    
    /**
     * Renames a backpack.
     * @param player
     * @param oldpack existing backpack
     * @param newpack new backpack name
     */
    public abstract void renameBackpack(String player, String oldpack, String newpack);
    
    public Map<String, PlayerBackpacks> getBackpacks() {
        return backpacks;
    }
    
    public void setBackpacks(Map<String, PlayerBackpacks> backpacks) {
        Validate.notNull(backpacks);
        this.backpacks = backpacks;
    }
    
    public abstract void loadAll();
    
}
