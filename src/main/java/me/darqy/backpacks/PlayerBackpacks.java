package me.darqy.backpacks;

import java.util.Collection;
import java.util.HashMap;

public class PlayerBackpacks {
    
    private HashMap<String, Backpack> backpacks = new HashMap<String, Backpack>();
    private String player;
    private BackpackManager manager;
    
    public PlayerBackpacks(BackpackManager manager, String player) {
        this.manager = manager;
        this.player = player;
    }

    public void setBackpack(String backpack, Backpack pack) {
        backpacks.put(backpack, pack);
    }

    public void closeBackpacks() {
        for (Backpack backpack : backpacks.values()) {
            backpack.closeAll();
        }
    }

    public int getBackpackCount() {
        return backpacks.values().size();
    }

    public Backpack getBackpack(String backpack) {
        if (!hasBackpack(backpack)) {
            manager.loadBackpack(player, backpack);
        }
        return backpacks.get(backpack);
    }

    public boolean hasBackpack(String backpack) {
        return backpacks.containsKey(backpack);
    }

    public String getPlayer() {
        return player;
    }

    public Backpack createBackpack(String backpack) {
        Backpack pack = new Backpack(backpack);
        backpacks.put(backpack, pack);
        return pack;
    }

    public Collection<Backpack> getBackpacks() {
        return backpacks.values();
    }

    public void renameBackpack(String oldBackpack, String newBackpack) {
        Backpack backpack = getBackpack(oldBackpack);
        if (backpack != null) {
            backpacks.remove(oldBackpack);
            backpack.rename(newBackpack);
            setBackpack(newBackpack, backpack);
        }
    }
    
}
