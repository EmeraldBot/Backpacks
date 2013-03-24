package me.darqy.backpacks;

import java.io.IOException;
import java.util.List;

public interface BackpackManager {
    
    /**
     * Saves all loaded backpacks to disk.
     * @throws IOException 
     */
    public void saveBackpacks() throws IOException;
    
    /**
     * Saves a specific backpack. Whether or not the data is then saved to
     * disk is up to the implementation.
     * @param player
     * @param backpack
     * @throws IOException 
     */
    public void saveBackpack(String player, String backpack) throws IOException;
    
    /**
     * Gets the amount of backpacks a player has. (loaded or not)
     * @param player
     * @return amount of backpacks a player has
     */
    public int getBackpackCount(String player);
    
    /**
     * A list of all the backpacks a player has. (loaded or not)
     * @param player
     * @return a List of backpack names
     */
    public List<String> getBackpackList(String player);
    
    /**
     * Loads a backpack from disk
     * @param player
     * @param backpack
     * @return whether or not the backpack storage was available
     */
    public boolean loadBackpack(String player, String backpack);
    
    /**
     * Closes all open inventory screens
     */
    public void closeBackpacks();
    
    /**
     * Gets the PlayerBackpacks object, which holds all loaded backpacks
     * for a player.
     * @param player
     * @return 
     */
    public PlayerBackpacks getPlayerBackpacks(String player);
    
    /**
     * Renames a backpack.
     * @param player
     * @param oldpack existing backpack
     * @param newpack new backpack name
     */
    public void renameBackpack(String player, String oldpack, String newpack);
    
}
