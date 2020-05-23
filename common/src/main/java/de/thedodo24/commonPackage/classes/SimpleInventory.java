package de.thedodo24.commonPackage.classes;

import de.thedodo24.commonPackage.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class SimpleInventory {

    private static List<SimpleInventory> simpleInventories = new ArrayList<>();

    private Map<String, Object> properties;

    public static SimpleInventory getSimpleInventory(Inventory inventory) {
        for(SimpleInventory simpleInventory : simpleInventories) {
            if(simpleInventory.getInventory().equals(inventory))
                return simpleInventory;
        }
        return null;
    }

    public SimpleInventory(SimpleInventory previousInventory, SimpleInventory nextInventory, String title, InventoryType inventoryType, boolean fillSpace) {
        properties = new HashMap<>();
        properties.put("previousInventory", previousInventory);
        properties.put("nextInventory", nextInventory);
        properties.put("inventory", Bukkit.createInventory(null, inventoryType, title));
        properties.put("fillSpace", fillSpace);
        simpleInventories.add(this);
    }

    public SimpleInventory(SimpleInventory previousInventory, SimpleInventory nextInventory, String title, int size, boolean fillSpace) {
        properties = new HashMap<>();
        properties.put("previousInventory", previousInventory);
        properties.put("nextInventory", nextInventory);
        properties.put("inventory", Bukkit.createInventory(null, size, title));
        properties.put("fillSpace", fillSpace);
        simpleInventories.add(this);
    }

    public void saveData(String key, Object value) {
        properties.put(key, value);
    }

    public Object getData(String key) {
        if(properties.containsKey(key))
            return properties.get(key);
        return null;
    }

    public String getDataAsString(String key) {
        if(properties.containsKey(key))
            return (String) properties.get(key);
        return null;
    }

    public Integer getDataAsInteger(String key) {
        if(properties.containsKey(key))
            return (Integer) properties.get(key);
        return null;
    }

    public Long getDataAsLong(String key) {
        if(properties.containsKey(key))
            return (Long) properties.get(key);
        return null;
    }

    public void setNextInventory(Inventory inv) {
        properties.replace("nextInventory", inv);
    }

    public void setPreviousInventory(Inventory inv) {
        properties.replace("previousInventory", inv);
    }

    public Inventory getNextInventory() {
        return (Inventory) properties.get("nextInventory");
    }

    public Inventory getPreviosInventory() {
        return (Inventory) properties.get("previousInventory");
    }

    public Inventory getInventory() {
        Inventory inv = (Inventory) properties.get("inventory");
        if((Boolean) properties.get("fillSpace"))
            for(int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) != null && !inv.getItem(i).getType().equals(Material.AIR)) {
                    inv.setItem(i, new ItemBuilder(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1)).modify().setDisplayName(" ").build());
                }
            }
        return inv;
    }

    public void setInventory(Inventory inventory) {
        properties.replace("inventory", inventory);
    }

    public void setInventory(String title, InventoryType type) {
        properties.replace("inventory", Bukkit.createInventory(null, type, title));
    }

    public void setInventory(String title, int size) {
        properties.replace("inventory", Bukkit.createInventory(null, size, title));
    }

    public boolean isFilled() {
        return (Boolean) properties.get("fillSpace");
    }

    public void setFilled(boolean fillSpace) {
        properties.replace("fillSpace", fillSpace);
    }

    public void setItem(ItemStack item, int value) {
        ((Inventory) properties.get("inventory")).setItem(value, item);
    }

    public void addItem(ItemStack... item) {
        ((Inventory) properties.get("inventory")).addItem(item);
    }


}
