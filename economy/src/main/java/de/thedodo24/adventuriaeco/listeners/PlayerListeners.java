package de.thedodo24.adventuriaeco.listeners;

import com.google.common.collect.Lists;
import de.thedodo24.adventuriaeco.Economy;
import de.thedodo24.commonPackage.economy.BankAccount;
import de.thedodo24.commonPackage.economy.BankType;
import de.thedodo24.commonPackage.player.User;
import de.thedodo24.commonPackage.utils.ItemBuilder;
import de.thedodo24.commonPackage.utils.SkullItems;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PlayerListeners implements Listener {

    private String bankPrefix = "§7§l| §2Bank §7» ";

    private String formatValue(double v) { return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(v).split(" ")[0] + " A"; }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Economy.getInstance().getArmorStandAddList().remove(p.getUniqueId());
        Economy.getInstance().getArmorStandDelList().remove(p.getUniqueId());
        Economy.getInstance().getChatListenerCreate().remove(p.getUniqueId());

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();
            if(item != null) {
                if(item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if(e.getSlotType() != InventoryType.SlotType.OUTSIDE) {
                        InventoryView inv = p.getOpenInventory();
                        String title = inv.getTitle();
                        switch(title) {
                            case "§2Was möchtest du tun?":
                                e.setCancelled(true);
                                if(item.getItemMeta().hasDisplayName()) {
                                    String displayName = item.getItemMeta().getDisplayName();
                                    if(item.getType().equals(Material.PLAYER_HEAD)) {
                                        if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                            if(displayName.equalsIgnoreCase("§aBankkonto erstellen")) {
                                                int size = Economy.getInstance().getManager().getBankManager().getOwnerBankAccounts(p.getUniqueId()).size();
                                                p.closeInventory();
                                                if(size < 3) {
                                                    if(Economy.getInstance().getChatListenerCreate().contains(p.getUniqueId()) || Economy.getInstance().getChatListenerTransfer().containsKey(p.getUniqueId())) {
                                                        p.sendMessage(bankPrefix + "§7Bitte gebe den Namen für dein Konto in den Chat ein. Um abzubrechen schreibe §cStop §7in den Chat.");
                                                        return;
                                                    }
                                                    Economy.getInstance().getChatListenerCreate().add(p.getUniqueId());
                                                    p.sendMessage(bankPrefix + "§7Gebe bitte den Namen für das §2Konto §7in den Chat ein. Um abzubrechen schreibe §cStop §7in den Chat.");
                                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                                } else {
                                                    p.sendMessage(bankPrefix + "§7Du hast bereits §23 Konten§7. Lösche zuerst ein Konto, bevor du ein Neues erstellen kannst.");
                                                }
                                            } else if(displayName.equalsIgnoreCase("§cBankkonto löschen")) {
                                                p.closeInventory();
                                                Inventory deleteBank = Bukkit.createInventory(null, InventoryType.HOPPER, "§cWelches Konto willst du löschen?");
                                                Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).forEach(bankAccount -> deleteBank.addItem(SkullItems.getMoneyBagSkull("§2" + bankAccount.getKey())));
                                                p.openInventory(deleteBank);
                                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                            } else if(displayName.equalsIgnoreCase("§2Kontostand anzeigen")) {
                                                p.closeInventory();
                                                int size = (Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).size() / 9) + 1;
                                                Inventory balanceAccounts = Bukkit.createInventory(null, 9*size, "§2Deine Konten");
                                                Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).forEach(bankAccount -> {
                                                    String value = formatValue(((Long) bankAccount.getBalance()).doubleValue() / 100);
                                                    ItemStack i = SkullItems.getMoneyBagSkull("§2" + bankAccount.getKey());
                                                    ItemMeta itemMeta = i.getItemMeta();
                                                    itemMeta.setLore(Lists.newArrayList("§7» Kontostand: §2" + value));
                                                    i.setItemMeta(itemMeta);
                                                    balanceAccounts.addItem(i);
                                                });
                                                p.openInventory(balanceAccounts);
                                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                            } else if(displayName.equalsIgnoreCase("§2Konten anzeigen") || displayName.equalsIgnoreCase("§aGeld einzahlen") ||
                                                    displayName.equalsIgnoreCase("§cGeld abheben") || displayName.equalsIgnoreCase("§7Geld überweisen")) {
                                                p.closeInventory();
                                                int size = (Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).size() / 9) + 1;
                                                Inventory listBanks = Bukkit.createInventory(null, size * 9,
                                                        (displayName.equalsIgnoreCase("§2Konten anzeigen") ? "§2Deine Konten" :
                                                                (displayName.equalsIgnoreCase("§aGeld einzahlen") ? "§aAuf welches Konto?" :
                                                                        (displayName.equalsIgnoreCase("§cGeld abheben") ? "§cVon welchem Konto?" : "§2Von welchem Konto?"))));
                                                Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).forEach(bankAccount -> listBanks.addItem(SkullItems.getMoneyBagSkull("§2" + bankAccount.getKey())));
                                                p.openInventory(listBanks);
                                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                            } else if(displayName.equalsIgnoreCase("§cInhaber hinzufügen") || displayName.equalsIgnoreCase("§6Teilhaber hinzufügen") ||
                                                    displayName.equalsIgnoreCase("§cInhaber entfernen") || displayName.equalsIgnoreCase("§6Teilhaber entfernen")) {
                                                p.closeInventory();
                                                int size = (Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).size() / 9) + 1;
                                                Inventory listBanks = Bukkit.createInventory(null, size * 9,
                                                        (displayName.equalsIgnoreCase("§cInhaber hinzufügen") ? "§cWähle das Konto aus." :
                                                                (displayName.equalsIgnoreCase("§6Teilhaber hinzufügen") ? "§6Wähle das Konto aus." :
                                                                        (displayName.equalsIgnoreCase("§cInhaber entfernen") ? "§cWähle ein Konto aus." : "§6Wähle ein Konto aus."))));
                                                Economy.getInstance().getManager().getBankManager().getBankAccounts(p.getUniqueId()).stream().filter(bankAccount -> bankAccount.getOwners().contains(p.getUniqueId())).forEach(bankAccount -> listBanks.addItem(SkullItems.getMoneyBagSkull("§2" + bankAccount.getKey())));
                                                p.openInventory(listBanks);
                                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                            }
                                        }
                                    } else if(item.getType().equals(Material.EMERALD)) {
                                        if(displayName.equalsIgnoreCase("§aSmaragd kaufen")) {
                                            User u = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                                            if((u.getBalance() - 25000) >= 0) {
                                                PlayerInventory pInventory = p.getInventory();
                                                AtomicInteger place = new AtomicInteger(-1);
                                                if(pInventory.firstEmpty() != -1) {
                                                    pInventory.all(Material.EMERALD).keySet().stream().filter(key -> pInventory.getItem(key).getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING) &&
                                                            pInventory.getItem(key).getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald"))
                                                            .forEach(key -> {
                                                                ItemStack stack = pInventory.getItem(key);
                                                                if((stack.getAmount()) + 1 <= 64) {
                                                                    place.set(key);
                                                                }
                                                            });
                                                    if(place.get() == -1)
                                                        place.set(pInventory.firstEmpty());
                                                } else {
                                                    pInventory.all(Material.EMERALD).keySet().stream().filter(key -> pInventory.getItem(key).getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING) &&
                                                            pInventory.getItem(key).getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald"))
                                                            .forEach(key -> {
                                                                ItemStack stack = pInventory.getItem(key);
                                                                if((stack.getAmount()) + 1 <= 64) {
                                                                    place.set(key);
                                                                }
                                                            });
                                                }
                                                if(place.get() != -1) {
                                                    ItemStack itemStack = new ItemBuilder(new ItemStack(Material.EMERALD)).modify().setDisplayName("§3250A").build();
                                                    if(pInventory.getItem(place.get()) != null)
                                                        itemStack.setAmount(pInventory.getItem(place.get()).getAmount() + 1);
                                                    else
                                                        itemStack.setAmount(1);
                                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                                    itemMeta.getPersistentDataContainer().set(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING, "emerald");
                                                    itemStack.setItemMeta(itemMeta);
                                                    Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).withdrawMoney(25000);
                                                    Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(25000);
                                                    pInventory.setItem(place.get(), itemStack);

                                                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                                } else {
                                                    p.sendMessage(bankPrefix + "§7Du hast keinen Platz in deinem Inventar.");
                                                }
                                            } else {
                                                p.sendMessage(bankPrefix + "§7Du hast nicht genügend Geld.");
                                            }
                                        } else if(displayName.equalsIgnoreCase("§cSmaragd verkaufen")) {
                                            PlayerInventory pInventory = p.getInventory();
                                            List<ItemStack> emeralds =  pInventory.all(Material.EMERALD).values().stream().filter(i -> (i.getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING)
                                                    && i.getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald")) ||
                                                    (i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equalsIgnoreCase("\u00A73250A"))).collect(Collectors.toList());
                                            if(emeralds.size() > 0) {
                                                int v = emeralds.stream().mapToInt(ItemStack::getAmount).sum();
                                                pInventory.all(Material.EMERALD).keySet().stream().filter(key ->
                                                        (pInventory.getItem(key).getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING) &&
                                                                pInventory.getItem(key).getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald")) ||
                                                                (pInventory.getItem(key).getItemMeta().hasDisplayName() && pInventory.getItem(key).getItemMeta().getDisplayName().equalsIgnoreCase("\u00A73250A")))
                                                        .forEach(key -> pInventory.setItem(key, new ItemStack(Material.AIR)));
                                                User u = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                                                u.depositMoney(v * 25000);
                                                Economy.getInstance().getManager().getBankManager().get("staatskasse").withdrawMoney(v * 25000);
                                                p.sendMessage(bankPrefix + "§7Du hast §2" + v + " Emeralds §7für §2" + formatValue(250 * v) + " §7verkauft.");
                                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                            } else {
                                                p.sendMessage(bankPrefix + "§7Du hast keine §3Emeralds §7in deinem Inventar.");
                                            }
                                        } else if(displayName.equalsIgnoreCase("§bDiamanten umtauschen")) {
                                            PlayerInventory pInventory = p.getInventory();
                                            //List<ItemStack> emeralds = pInventory.all(Material.EMERALD).values().stream().filter(i ->
                                            //        ((!i.getItemMeta().getDisplayName().equalsIgnoreCase("\u00A73250A")) && (!(i.getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING)
                                            //                && i.getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald"))))).collect(Collectors.toList());
                                            List<ItemStack> diamonds = new ArrayList<>(pInventory.all(Material.DIAMOND).values());
                                            if(diamonds.size() > 0) {
                                                int v = diamonds.stream().mapToInt(ItemStack::getAmount).sum();
                                                int stacks = v / 5;
                                                if(stacks > 0) {
                                                    AtomicInteger place = new AtomicInteger(-1);
                                                    if(pInventory.firstEmpty() != -1) {
                                                        pInventory.all(Material.EMERALD).keySet().stream().filter(key -> pInventory.getItem(key).getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING) &&
                                                                pInventory.getItem(key).getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald"))
                                                                .forEach(key -> {
                                                                    ItemStack stack = pInventory.getItem(key);
                                                                    if((stack.getAmount() + 1) <= 64) {
                                                                        place.set(key);
                                                                    }
                                                                });
                                                        if(place.get() == -1)
                                                            place.set(pInventory.firstEmpty());
                                                    } else {
                                                        pInventory.all(Material.EMERALD).keySet().stream().filter(key -> pInventory.getItem(key).getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING) &&
                                                                pInventory.getItem(key).getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald"))
                                                                .forEach(key -> {
                                                                    ItemStack stack = pInventory.getItem(key);
                                                                    if((stack.getAmount() + 1) <= 64) {
                                                                        place.set(key);
                                                                    }
                                                                });
                                                    }
                                                    if(place.get() != -1) {
                                                        AtomicInteger nextStack = new AtomicInteger(5);
                                                        //.filter(key -> ((!pInventory.getItem(key).getItemMeta().getDisplayName().equalsIgnoreCase("\u00A73250A")) && (!(pInventory.getItem(key).getItemMeta().getPersistentDataContainer().has(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING)
                                                        //                                                                && pInventory.getItem(key).getItemMeta().getPersistentDataContainer().get(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase("emerald")))))
                                                        pInventory.all(Material.DIAMOND).keySet()
                                                                .forEach(key -> {
                                                                    int stack = pInventory.getItem(key).getAmount();
                                                                    if(nextStack.get() > 0) {
                                                                        if ((stack - 5) < 0){
                                                                            nextStack.set(nextStack.get() - stack);
                                                                            pInventory.setItem(key, new ItemStack(Material.AIR));
                                                                        } else {
                                                                            pInventory.setItem(key, new ItemStack(Material.DIAMOND, stack - 5));
                                                                            nextStack.set(0);
                                                                        }
                                                                    }
                                                                });
                                                        p.sendMessage(bankPrefix + "§7Du hast §25 Emeralds §7umgetauscht.");
                                                        ItemStack itemStack = new ItemBuilder(new ItemStack(Material.EMERALD)).modify().setDisplayName("§3250A").build();
                                                        if(pInventory.getItem(place.get()) != null)
                                                            itemStack.setAmount(pInventory.getItem(place.get()).getAmount() + 1);
                                                        else
                                                            itemStack.setAmount(1);
                                                        ItemMeta itemMeta = itemStack.getItemMeta();
                                                        itemMeta.getPersistentDataContainer().set(Economy.getInstance().getNamespacedKey(), PersistentDataType.STRING, "emerald");
                                                        itemStack.setItemMeta(itemMeta);
                                                        pInventory.setItem(place.get(), itemStack);
                                                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                                    } else {
                                                        p.sendMessage(bankPrefix + "§7Du hast kein Platz in deinem Inventar.");
                                                    }
                                                } else {
                                                    p.sendMessage(bankPrefix + "§7Du hast nicht mindestens §a5 Emeralds §7in deinem Inventar.");
                                                }
                                            } else {
                                                p.sendMessage(bankPrefix + "§7Du hast keine §bDiamanten §7in deinem Inventar.");
                                            }
                                        }
                                    }
                                }
                                return;
                            case "§2Deine Konten":
                                e.setCancelled(true);
                                return;
                            case "§cWelches Konto willst du löschen?":
                                e.setCancelled(true);
                                if(item.getType().equals(Material.PLAYER_HEAD)) {
                                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                        p.closeInventory();
                                        String displayName = item.getItemMeta().getDisplayName();
                                        String bankAccount = displayName.substring(2);
                                        BankAccount account = Economy.getInstance().getManager().getBankManager().get(bankAccount);
                                        Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId()).depositMoney(account.getBalance());
                                        Economy.getInstance().getManager().getBankManager().delete(account.getKey());
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + bankAccount + " §7wurde gelöscht.");
                                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                    }
                                }
                                return;
                            case "§aAuf welches Konto?":
                                e.setCancelled(true);
                                if(item.getType().equals(Material.PLAYER_HEAD)) {
                                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                        p.closeInventory();
                                        String displayName = item.getItemMeta().getDisplayName();
                                        String bankAccount = displayName.substring(2);
                                        Inventory moneyList = getMoneyInventory("§a–‒ " + bankAccount);
                                        p.openInventory(moneyList);
                                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                    }
                                }
                                return;
                            case "§cVon welchem Konto?":
                                e.setCancelled(true);
                                if(item.getType().equals(Material.PLAYER_HEAD)) {
                                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                        p.closeInventory();
                                        String displayName = item.getItemMeta().getDisplayName();
                                        String bankAccount = displayName.substring(2);
                                        Inventory moneyList = getMoneyInventory("§c–‒ " + bankAccount);
                                        p.openInventory(moneyList);
                                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                    }
                                }
                                return;
                            case "§2Von welchem Konto?":
                                e.setCancelled(true);
                                if(item.getType().equals(Material.PLAYER_HEAD)) {
                                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                        p.closeInventory();
                                        if(Economy.getInstance().getChatListenerTransfer().containsKey(p.getUniqueId()) || Economy.getInstance().getChatListenerCreate().contains(p.getUniqueId())) {
                                            p.sendMessage(bankPrefix + "§7Bitte gebe den §2Kontonamen §7in den Chat ein. Um abzubrechen schreibe §cStop §7in den Chat.");
                                            return;
                                        }
                                        Economy.getInstance().getChatListenerTransfer().put(p.getUniqueId(), item.getItemMeta().getDisplayName().substring(2));
                                        p.sendMessage(bankPrefix + "§7Gebe bitte den §2Kontonamen §7in den Chat ein. Um abzubrechen schreibe §cStop §7in den Chat.");
                                    }
                                }
                                return;
                            case "§cWähle das Konto aus.":
                            case "§6Wähle das Konto aus.":
                                e.setCancelled(true);
                                if(item.getType().equals(Material.PLAYER_HEAD)) {
                                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                        p.closeInventory();
                                        int onlinePlayer = Bukkit.getOnlinePlayers().size();
                                        int inventorySize = (onlinePlayer / 9) + 1;
                                        String titleNewInventory = item.getItemMeta().getDisplayName().substring(2);
                                        BankAccount account = Economy.getInstance().getManager().getBankManager().get(titleNewInventory);
                                        Inventory playerInventory = Bukkit.createInventory(null, inventorySize*9, (title.equalsIgnoreCase("§cWähle das Konto aus.") ? "§cInhaber hinzufügen: " + titleNewInventory : "§6Teilhaber hinzufügen: " + titleNewInventory));
                                        if(title.equalsIgnoreCase("§cWähle das Konto aus."))
                                            Bukkit.getOnlinePlayers().stream().filter(player -> !account.getOwners().contains(player.getUniqueId())).forEach(player -> {
                                                if(!player.getUniqueId().equals(p.getUniqueId())) {
                                                    playerInventory.addItem(SkullItems.getPlayerHead(player));
                                                }
                                            });
                                        else
                                            Bukkit.getOnlinePlayers().stream().filter(player -> !account.getMembers().contains(player.getUniqueId())).forEach(player -> {
                                                if(!player.getUniqueId().equals(p.getUniqueId())) {
                                                    playerInventory.addItem(SkullItems.getPlayerHead(player));
                                                }
                                            });
                                        p.openInventory(playerInventory);
                                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                    }
                                }
                                return;
                            case "§cWähle ein Konto aus.":
                            case "§6Wähle ein Konto aus.":
                                e.setCancelled(true);
                                if(item.getType().equals(Material.PLAYER_HEAD)) {
                                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                        p.closeInventory();
                                        String titleNewInventory = item.getItemMeta().getDisplayName().substring(2);
                                        BankAccount account = Economy.getInstance().getManager().getBankManager().get(titleNewInventory);
                                        int inventorySize;
                                        if(title.equalsIgnoreCase("§cWähle ein Konto aus."))
                                            inventorySize = (account.getOwners().size() / 9) + 1;
                                        else
                                            inventorySize = (account.getMembers().size() / 9) + 1;
                                        Inventory playerInventory = Bukkit.createInventory(null, inventorySize*9, (title.equalsIgnoreCase("§cWähle ein Konto aus.") ? "§cInhaber entfernen: " + titleNewInventory : "§6Teilhaber entfernen: " + titleNewInventory));
                                        if(title.equalsIgnoreCase("§cWähle ein Konto aus."))
                                            account.getOwners().stream().map(Bukkit::getOfflinePlayer).forEach(offlinePlayer -> {
                                                if(!offlinePlayer.getUniqueId().equals(p.getUniqueId())) {
                                                    playerInventory.addItem(SkullItems.getPlayerHead(offlinePlayer));
                                                }
                                            });
                                        else
                                            account.getMembers().stream().map(Bukkit::getOfflinePlayer).forEach(offlinePlayer -> {
                                                if(!offlinePlayer.getUniqueId().equals(p.getUniqueId())) {
                                                    playerInventory.addItem(SkullItems.getPlayerHead(offlinePlayer));
                                                }
                                            });
                                        p.openInventory(playerInventory);
                                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
                                    }
                                }
                                return;
                        }
                        if(title.startsWith("§cInhaber hinzufügen: ")) {
                            e.setCancelled(true);
                            if(item.getType().equals(Material.PLAYER_HEAD)) {
                                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    p.closeInventory();
                                    String bankAccount = title.substring(22);
                                    String name = item.getItemMeta().getDisplayName().substring(2);
                                    User toAdd = Economy.getInstance().getManager().getPlayerManager().getByName(name);
                                    BankAccount account = Economy.getInstance().getManager().getBankManager().get(bankAccount);
                                    if(account != null) {
                                        if(!account.getOwners().contains(toAdd.getKey()) && !account.getMembers().contains(toAdd.getKey())) {
                                            account.getOwners().add(toAdd.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7wurde als §cInhaber §7hinzugefügt.");
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        } else {
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7ist bereits §cInhaber§7 oder §6Teilhaber§7.");
                                        }
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + bankAccount + " §7existiert nicht.");
                                    }
                                }
                            }
                        } else if(title.startsWith("§6Teilhaber hinzufügen: ")) {
                            e.setCancelled(true);
                            if(item.getType().equals(Material.PLAYER_HEAD)) {
                                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    p.closeInventory();
                                    String bankAccount = title.substring(24);
                                    String name = item.getItemMeta().getDisplayName().substring(2);
                                    User toAdd = Economy.getInstance().getManager().getPlayerManager().getByName(name);
                                    BankAccount account = Economy.getInstance().getManager().getBankManager().get(bankAccount);
                                    if(account != null) {
                                        if(!account.getOwners().contains(toAdd.getKey()) && !account.getMembers().contains(toAdd.getKey())) {
                                            account.getMembers().add(toAdd.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7wurde als §6Teilhaber §7hinzugefügt.");
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        } else {
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7ist bereits §cInhaber§7 oder §6Teilhaber§7.");
                                        }
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + bankAccount + " §7existiert nicht.");
                                    }
                                }
                            }
                        } else if(title.startsWith("§cInhaber entfernen: ")) {
                            e.setCancelled(true);
                            if(item.getType().equals(Material.PLAYER_HEAD)) {
                                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    p.closeInventory();
                                    String bankAccount = title.substring(21);
                                    String name = item.getItemMeta().getDisplayName().substring(2);
                                    User toAdd = Economy.getInstance().getManager().getPlayerManager().getByName(name);
                                    BankAccount account = Economy.getInstance().getManager().getBankManager().get(bankAccount);
                                    if(account != null) {
                                        if(account.getOwners().contains(toAdd.getKey())) {
                                            account.getOwners().remove(toAdd.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7wurde als §cInhaber §7entfernt");
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        } else {
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7ist kein §cInhaber§7.");
                                        }
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + bankAccount + " §7existiert nicht.");
                                    }
                                }
                            }
                        } else if(title.startsWith("§6Teilhaber entfernen: ")) {
                            e.setCancelled(true);
                            if(item.getType().equals(Material.PLAYER_HEAD)) {
                                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    p.closeInventory();
                                    String bankAccount = title.substring(23);
                                    String name = item.getItemMeta().getDisplayName().substring(2);
                                    User toAdd = Economy.getInstance().getManager().getPlayerManager().getByName(name);
                                    BankAccount account = Economy.getInstance().getManager().getBankManager().get(bankAccount);
                                    if(account != null) {
                                        if(account.getMembers().contains(toAdd.getKey())) {
                                            account.getMembers().remove(toAdd.getKey());
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7wurde als §6Teilhaber §7entfernt.");
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        } else {
                                            p.sendMessage(bankPrefix + "§7Der Spieler §2" + toAdd.getName() + " §7ist kein §6Teilhaber§7.");
                                        }
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + bankAccount + " §7existiert nicht.");
                                    }
                                }
                            }
                        } else if(title.startsWith("§a–‒")) {
                            e.setCancelled(true);
                            if(item.getType().equals(Material.PLAYER_HEAD)) {
                                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    p.closeInventory();
                                    String bankAccount = title.substring(5);
                                    int value = 0;
                                    try {
                                        value = Integer.parseInt(item.getItemMeta().getDisplayName().substring(2, item.getItemMeta().getDisplayName().length() - 2).replace(".", ""));
                                    } catch(NumberFormatException ignore) {  }
                                    BankAccount account = Economy.getInstance().getManager().getBankManager().get(bankAccount);
                                    if(account != null) {
                                        User user = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                                        long longValue = ((Integer) value).longValue() * 100;
                                        long taxes = (long) (longValue * 0.0025);
                                        if(taxes == 0)
                                            taxes = 1;
                                        if((user.getBalance() - longValue) >= 0) {
                                            user.withdrawMoney(longValue);
                                            account.depositMoney(longValue - taxes);
                                            Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(taxes);
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            Economy.getInstance().getManager().getBankManager().save(Economy.getInstance().getManager().getBankManager().get("staatskasse"));
                                            Economy.getInstance().getManager().getPlayerManager().save(user);
                                            p.sendMessage(bankPrefix + "§7Du hast §2" + formatValue(((Long) longValue).doubleValue() / 100) + " §7auf das Konto §2" + account.getKey() + " §7eingezahlt und §2" + formatValue(((Long) taxes).doubleValue() / 100) + " §7Steuern gezahlt.");
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        } else {
                                            p.sendMessage(bankPrefix + "§7Du hast nicht genügend Geld.");
                                        }
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + bankAccount + " §7existiert nicht.");
                                    }
                                }
                            }
                        } else if(title.startsWith("§c–‒")) {
                            e.setCancelled(true);
                            if(item.getType().equals(Material.PLAYER_HEAD)) {
                                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    p.closeInventory();
                                    String bankAccount = title.substring(5);
                                    int value = 0;
                                    try {
                                        value = Integer.parseInt(item.getItemMeta().getDisplayName().substring(2, item.getItemMeta().getDisplayName().length() - 2).replace(".", ""));
                                    } catch(NumberFormatException ignore) {  }
                                    BankAccount account = Economy.getInstance().getManager().getBankManager().get(bankAccount);
                                    if(account != null) {
                                        User user = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                                        long longValue = ((Integer) value).longValue() * 100;
                                        long taxes = (long) (longValue * 0.0025);
                                        if(taxes == 0)
                                            taxes = 1;
                                        if((account.getBalance() - longValue) >= 0) {
                                            account.withdrawMoney(longValue);
                                            user.depositMoney(longValue - taxes);
                                            Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(taxes);
                                            Economy.getInstance().getManager().getBankManager().save(account);
                                            Economy.getInstance().getManager().getBankManager().save(Economy.getInstance().getManager().getBankManager().get("staatskasse"));
                                            Economy.getInstance().getManager().getPlayerManager().save(user);
                                            p.sendMessage(bankPrefix + "§7Du hast §2" + formatValue(((Long) longValue).doubleValue() / 100) + " §7vom Konto §2" + account.getKey() + " §7abgebucht und §2" + formatValue(((Long) taxes).doubleValue() / 100) + " §7Steuern gezahlt.");
                                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        } else {
                                            p.sendMessage(bankPrefix + "§7Das Bankkonto hat nicht genügend §2Guthaben§7.");
                                        }
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + bankAccount + " §7existiert nicht.");
                                    }
                                }
                            }
                        } else if(title.startsWith("§7–‒")) {
                            e.setCancelled(true);
                            if(item.getType().equals(Material.PLAYER_HEAD)) {
                                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                    p.closeInventory();
                                    String[] accounts = title.split("§8-§7");
                                    String accountFrom = accounts[0].substring(5, accounts[0].length() - 1);
                                    String accountTo = accounts[1].substring(1);
                                    int value = 0;
                                    try {
                                        value = Integer.parseInt(item.getItemMeta().getDisplayName().substring(2, item.getItemMeta().getDisplayName().length() - 2).replace(".", ""));
                                    } catch(NumberFormatException ignore) {  }
                                    BankAccount bankAccountFrom = Economy.getInstance().getManager().getBankManager().get(accountFrom);
                                    if(bankAccountFrom != null) {
                                        BankAccount bankAccountTo = Economy.getInstance().getManager().getBankManager().get(accountTo);
                                        if(bankAccountTo != null) {
                                            long longValue = ((Integer) value).longValue() * 100;
                                            long taxes = (long) (longValue * 0.0025);
                                            if(taxes == 0)
                                                taxes = 1;
                                            if((bankAccountFrom.getBalance() - longValue) >= 0) {
                                                bankAccountFrom.withdrawMoney(longValue);
                                                bankAccountTo.depositMoney(longValue - taxes);
                                                Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(taxes);
                                                Economy.getInstance().getManager().getBankManager().save(bankAccountFrom);
                                                Economy.getInstance().getManager().getBankManager().save(bankAccountTo);
                                                Economy.getInstance().getManager().getBankManager().save(Economy.getInstance().getManager().getBankManager().get("staatskasse"));
                                                p.sendMessage(bankPrefix + "§7Du hast §2" + formatValue(((Long) longValue).doubleValue() / 100) + " §7auf das Konto §2" + accountTo + " §7überwiesen und §2" + formatValue(((Long) taxes).doubleValue() / 100) + " §7Steuern gezahlt.");
                                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                            } else {
                                                p.sendMessage(bankPrefix + "§7Das Bankkonto hat nicht genügend §2Guthaben§7.");
                                            }
                                        } else {
                                            p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + accountTo + " §7existiert nicht.");
                                        }
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Das Bankkonto §2" + accountFrom + " §7existiert nicht.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if(!p.getWorld().equals(Bukkit.getWorld("Eventwelt"))) {
            User user = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
            long tax = (long) (user.getBalance() * 0.05);
            user.withdrawMoney(tax);
            Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(tax);
            p.sendMessage("§7Du hast §a" + formatValue(((Long) tax).doubleValue() / 100) + " §7verloren.");
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onAsyncChat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        if(Economy.getInstance().getChatListenerCreate().contains(p.getUniqueId())) {
            e.setCancelled(true);
            String msg = e.getMessage();
            if(msg.equalsIgnoreCase("stop")) {
                Economy.getInstance().getChatListenerCreate().remove(p.getUniqueId());
                p.sendMessage(bankPrefix + "§7Du hast den Vorgang §cabgebrochen§7.");
                return;
            }
            if(msg.split(" ").length == 1) {
                int size = Economy.getInstance().getManager().getBankManager().getOwnerBankAccounts(p.getUniqueId()).size();
                if(msg.length() <= 16) {
                    if(!(msg.contains("ä") || msg.contains("ü") || msg.contains("ö") || msg.contains("ß"))) {
                        Economy.getInstance().getChatListenerCreate().remove(p.getUniqueId());
                        User user = Economy.getInstance().getManager().getPlayerManager().get(p.getUniqueId());
                        BankAccount testAccount = Economy.getInstance().getManager().getBankManager().get(msg.toLowerCase());
                        if(testAccount == null) {
                            switch(size) {
                                case 0:
                                    p.sendMessage(bankPrefix + "§7Dein erstes Bankkonto §2" + msg.toLowerCase() + " §7wurde §2kostenlos §7erstellt.");
                                    break;
                                case 1:
                                    if((user.getBalance() - 500000) >= 0) {
                                        p.sendMessage(bankPrefix + "§7Dein zweites Bankkonto §2" + msg.toLowerCase() + " §7wurde für einen Aufpreis von §25.000 A §7erstellt.");
                                        user.withdrawMoney(500000);
                                        Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(500000);
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Du hast nicht genügend Geld.");
                                        return;
                                    }
                                    break;
                                case 2:
                                    if((user.getBalance() - 1000000) >= 0) {
                                        p.sendMessage(bankPrefix + "§7Dein drittes Bankkonto §2" + msg.toLowerCase() + " §7wurde für einen Aufpreis von §210.000 A §7erstellt.");
                                        user.withdrawMoney(1000000);
                                        Economy.getInstance().getManager().getBankManager().get("staatskasse").depositMoney(1000000);
                                    } else {
                                        p.sendMessage(bankPrefix + "§7Du hast nicht genügend Geld.");
                                        return;
                                    }
                                    break;
                            }
                            BankAccount bankAccount = Economy.getInstance().getManager().getBankManager().getOrGenerate(msg.toLowerCase(), key -> {
                                BankAccount account = new BankAccount(key);
                                account.setBankType(BankType.BANK);
                                account.setOwners(Lists.newArrayList(p.getUniqueId()));
                                account.setMembers(Lists.newArrayList());
                                account.setBalance(0);
                                return account;
                            });
                            Economy.getInstance().getManager().getBankManager().save(bankAccount);
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        } else {
                            p.sendMessage(bankPrefix + "§7Ein Bankkonto mit dem Namen §2" + msg.toLowerCase() + " §7existiert bereits.");
                        }
                    } else {
                        p.sendMessage(bankPrefix + "§7Benutze bitte keine §2Umlaute §7im Kontonamen.");
                    }
                } else {
                    p.sendMessage(bankPrefix + "§7Der Name darf maximal §216 Zeichen §7lang sein.");
                }
            } else {
                p.sendMessage(bankPrefix + "§7Bitte gebe ein Wort als §2Kontonamen §7an.");
            }
        } else if(Economy.getInstance().getChatListenerTransfer().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            String msg = e.getMessage();
            if(msg.equalsIgnoreCase("stop")) {
                Economy.getInstance().getChatListenerTransfer().remove(p.getUniqueId());
                p.sendMessage(bankPrefix + "§7Du hast den Vorgang §cabgebrochen§7.");
                return;
            }
            if(msg.split(" ").length == 1) {
                String name = Economy.getInstance().getChatListenerTransfer().get(p.getUniqueId());
                Economy.getInstance().getChatListenerTransfer().remove(p.getUniqueId());
                BankAccount account = Economy.getInstance().getManager().getBankManager().get(msg.toLowerCase());
                if(account != null) {
                    Inventory moneyInventory = getMoneyInventory("§7–‒ " + name + " §8-§7 " + account.getKey());
                    p.openInventory(moneyInventory);
                } else {
                    p.sendMessage(bankPrefix + "§7Das Konto §2" + msg.toLowerCase() + " §7existiert nicht.");
                }
            } else {
                p.sendMessage(bankPrefix + "§7Bitte gebe einen gültigen §2Kontonamen §7ein.");
            }
        }
    }


    private Inventory getMoneyInventory(String title) {
        Inventory inv = Bukkit.createInventory(null, 9, title);
        inv.addItem(SkullItems.getMoneySkull("§21 A"));
        inv.addItem(SkullItems.getMoneySkull("§210 A"));
        inv.addItem(SkullItems.getMoneySkull("§2100 A"));
        inv.addItem(SkullItems.getMoneySkull("§21.000 A"));
        inv.addItem(SkullItems.getMoneySkull("§25.000 A"));
        inv.addItem(SkullItems.getMoneySkull("§210.000 A"));
        inv.addItem(SkullItems.getMoneySkull("§225.000 A"));
        inv.addItem(SkullItems.getMoneySkull("§250.000 A"));
        inv.addItem(SkullItems.getMoneySkull("§2100.000 A"));
        return inv;
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if(e.getRightClicked() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) e.getRightClicked();
            UUID uuid = armorStand.getUniqueId();
            Player p = e.getPlayer();
            if(Economy.getInstance().getArmorStandAddList().contains(p.getUniqueId())) {
                e.setCancelled(true);
                Economy.getInstance().getArmorStandAddList().remove(p.getUniqueId());
                if(Economy.getInstance().getManager().getArmorStandManager().checkArmorStand(uuid)) {
                    p.sendMessage(bankPrefix + "§7Der Armorstand existiert bereits in der Datenbank.");
                    return;
                }
                Economy.getInstance().getManager().getArmorStandManager().addArmorStand(uuid);
                p.sendMessage(bankPrefix + "§7Der Armorstand wurde hinzugefügt.");
                return;
            }
            if(Economy.getInstance().getArmorStandDelList().contains(p.getUniqueId())) {
                e.setCancelled(true);
                Economy.getInstance().getArmorStandDelList().remove(p.getUniqueId());
                if(Economy.getInstance().getManager().getArmorStandManager().checkArmorStand(uuid)) {
                    Economy.getInstance().getManager().getArmorStandManager().removeArmorStand(uuid);
                    p.sendMessage(bankPrefix + "§7Der Armor Stand wurde §cgelöscht§7.");
                } else {
                    p.sendMessage(bankPrefix + "§7Der Armor Stand ist nicht registriert.");
                }
                return;
            }
            if(Economy.getInstance().getManager().getArmorStandManager().checkArmorStand(uuid)) {
                e.setCancelled(true);
                Inventory inv = Bukkit.createInventory(null, 4*9, "§2Was möchtest du tun?");
                for(int i = 0; i < 9; i++) {
                    inv.setItem(i, new ItemBuilder(new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1)).modify().setDisplayName(" ").build());
                }
                inv.setItem(10, SkullItems.getGreenPlusSkull("§aBankkonto erstellen"));
                inv.setItem(11, SkullItems.getRedMinusSkull("§cBankkonto löschen"));
                inv.setItem(12, SkullItems.getMoneyBagSkull("§2Kontostand anzeigen"));
                inv.setItem(13, SkullItems.getBooksSkull("§2Konten anzeigen"));
                inv.setItem(14, SkullItems.getArrowUpSkull("§aGeld einzahlen"));
                inv.setItem(15, SkullItems.getArrowDownSkull("§cGeld abheben"));
                inv.setItem(16, SkullItems.getArrowRightSkull("§7Geld überweisen"));

                inv.setItem(20, SkullItems.getRedPlusSkull("§cInhaber hinzufügen"));
                inv.setItem(21, SkullItems.getOrangePlusSkull("§6Teilhaber hinzufügen"));
                inv.setItem(22, new ItemBuilder(new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1)).modify().setDisplayName(" ").build());
                inv.setItem(23, SkullItems.getRedMinusSkull("§cInhaber entfernen"));
                inv.setItem(24, SkullItems.getOrangeMinusSkull("§6Teilhaber entfernen"));
                //inv.setItem(33, SkullItems.getMoneySkull("§aSmaragde verkaufen"));
                inv.setItem(27, new ItemBuilder(new ItemStack(Material.DIAMOND, 1)).modify().setDisplayName("§bDiamanten umtauschen").build());
                inv.setItem(28, new ItemBuilder(new ItemStack(Material.EMERALD, 1)).modify().setDisplayName("§aSmaragd kaufen").build());
                inv.setItem(34, new ItemBuilder(new ItemStack(Material.EMERALD, 1)).modify().setDisplayName("§cSmaragd verkaufen").build());
                for(int i = 27; i < 36; i++) {
                    if(i != 27 && i != 28 && i != 34)
                        inv.setItem(i, new ItemBuilder(new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1)).modify().setDisplayName(" ").build());
                }
                p.openInventory(inv);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }
    }
}
