package de.thedodo24.commonPackage.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkullItems {

    private static ItemStack get(String b64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        UUID hash = new UUID(b64.hashCode(), b64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:\"" + hash + "\",Properties:{textures:[{Value:\"" + b64 + "\"}]}}}");
    }

    private static ItemStack getHead(OfflinePlayer p) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName("§2" + p.getName());
        meta.setOwningPlayer(p);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack setDisplayName(ItemStack itemStack, String displayName) {
        ItemMeta m = itemStack.getItemMeta();
        m.setDisplayName(displayName);
        itemStack.setItemMeta(m);
        return itemStack;
    }

    private static ItemStack setDisplayName(ItemStack itemStack, String displayName, List<String> lore) {
        ItemMeta m = itemStack.getItemMeta();
        m.setDisplayName(displayName);
        m.setLore(lore);
        itemStack.setItemMeta(m);
        return itemStack;
    }

    public static ItemStack getPlayerHead(OfflinePlayer p) {
        return getHead(p);
    }

    public static ItemStack getGreenPlusSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getRedPlusSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWM3MzFjM2M3MjNmNjdkMmNmYjFhMTE5MmI5NDcwODZmYmEzMmFlYTQ3MmQzNDdhNWVkNWQ3NjQyZjczYiJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getMoneySkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQwMDVlYmJmOTgxN2Q2OTI4ZGU4YmM1ZjdkMWMzODkyNzYwMjBhYzg3NjQ3ZDI4YWI4Zjk5ZWIzOWZmZGU3NiJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getOrangePlusSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTI1MGIzY2NlNzY2MzVlZjRjN2E4OGIyYzU5N2JkMjc0OTg2OGQ3OGY1YWZhNTY2MTU3YzI2MTJhZTQxMjAifX19";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getOrangeMinusSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWNmZjkxZGM5OWQ1ODI4MDIzZWVkZjg3Mzc5OWQyNTUzNWRhZGU2NGEyZTE2YTNiNDk4YjQxMTNlYWZkNDk2NiJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getRedMinusSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getArrowUpSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRhMDI3NDc3MTk3YzZmZDdhZDMzMDE0NTQ2ZGUzOTJiNGE1MWM2MzRlYTY4YzhiN2JjYzAxMzFjODNlM2YifX19";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getArrowDownSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4NTJiZjYxNmYzMWVkNjdjMzdkZTRiMGJhYTJjNWY4ZDhmY2E4MmU3MmRiY2FmY2JhNjY5NTZhODFjNCJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getMoneyBagSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY3NWQxYjc4NWQxOGQ0N2IzZWE4ZjBhN2UwZmQ0YTFmYWU5ZTdkMzIzY2YzYjEzOGM4Yzc4Y2ZlMjRlZTU5In19fQ==";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getBooksSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWNkMDgxMTY4Y2E4NjYwNGZjZjM3ODAwMzQ4Y2MxNzJjZTc0MDczOWRiM2NjMDgwZjA3ZjFhN2ZiZGZmZjQ4OSJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getArrowRightSkull(String displayName) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMyY2E2NjA1NmI3Mjg2M2U5OGY3ZjMyYmQ3ZDk0YzdhMGQ3OTZhZjY5MWM5YWMzYTkxMzYzMzEzNTIyODhmOSJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName);
    }

    public static ItemStack getGreenOneSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODg5OTE2OTc0Njk2NTNjOWFmODM1MmZkZjE4ZDBjYzljNjc3NjNjZmU2NjE3NWMxNTU2YWVkMzMyNDZjNyJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }

    public static ItemStack getGreenTwoSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ5NmMxNjJkN2M5ZTFiYzhjZjM2M2YxYmZhNmY0YjJlZTVkZWM2MjI2YzIyOGY1MmViNjVkOTZhNDYzNWMifX19";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }

    public static ItemStack getGreenThreeSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQyMjZmMmViNjRhYmM4NmIzOGI2MWQxNDk3NzY0Y2JhMDNkMTc4YWZjMzNiN2I4MDIzY2Y0OGI0OTMxMSJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }

    public static ItemStack getGreenFourSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkyMGVjY2UxYzhjZGU1ZGJjYTU5MzhjNTM4NGY3MTRlNTViZWU0Y2NhODY2YjcyODNiOTVkNjllZWQzZDJjIn19fQ==";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }

    public static ItemStack getGreenFiveSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJjMTQyYWY1OWYyOWViMzVhYjI5YzZhNDVlMzM2MzVkY2ZjMmE5NTZkYmQ0ZDJlNTU3MmIwZDM4ODkxYjM1NCJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }
    public static ItemStack getGreenSixSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjRkZGIwM2FhOGM1ODQxNjhjNjNlY2UzMzdhZWZiMjgxNzc0Mzc3ZGI3MjMzNzI5N2RlMjU4YjRjY2E2ZmJhNCJ9fX0=";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }
    public static ItemStack getGreenSevenSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdkZTcwYjg4MzY4Y2UyM2ExYWM2ZDFjNGFkOTEzMTQ4MGYyZWUyMDVmZDRhODVhYjI0MTdhZjdmNmJkOTAifX19";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }
    public static ItemStack getGreenEightSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI2NDdhZTQ3YjZiNTFmNWE0NWViM2RjYWZhOWI4OGYyODhlZGU5Y2ViZGI1MmExNTllMzExMGU2YjgxMThlIn19fQ==";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }
    public static ItemStack getGreenNineSkull(String displayName, List<String> lore) {
        String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFlNDYxYTQ0MzQxOTZkMzcyOTZhZDVhZGY2ZDlkNTc0NGEwNDE1ZGM2MWM0NzVhNmRmYTYyODU4MTQwNTIifX19";
        ItemStack skull = get(b64);
        return setDisplayName(skull, displayName, lore);
    }


    public static ItemStack getNumberSkull(int week, String displayName, List<String> lore) {
        switch(week) {
            case 1:
                return getGreenOneSkull(displayName, lore);
            case 2:
                return getGreenTwoSkull(displayName, lore);
            case 3:
                return getGreenThreeSkull(displayName, lore);
            case 4:
                return getGreenFourSkull(displayName, lore);
            case 5:
                return getGreenFiveSkull(displayName, lore);
            case 6:
                return getGreenSixSkull(displayName, lore);
            case 7:
                return getGreenSevenSkull(displayName, lore);
            case 8:
                return getGreenEightSkull(displayName, lore);
            case 9:
                return getGreenNineSkull(displayName, lore);
            default:
                return new ItemStack(Material.AIR);
        }
    }
}
