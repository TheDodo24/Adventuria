package de.thedodo24.adventuriaeco.listeners;

import de.thedodo24.adventuriaeco.Economy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class WorldListeners implements Listener {

    @EventHandler
    public void onEntityDestroy(EntityDamageByEntityEvent e) {
        if(e.getEntityType().equals(EntityType.ARMOR_STAND)) {
            if(Economy.getInstance().getManager().getArmorStandManager().getArmorStands().contains(e.getEntity().getUniqueId())) {
                e.setCancelled(true);
                if(e.getDamager() instanceof Player)
                    e.getDamager().sendMessage("§7§l| §2Bank §7» Du musst zuerst den §2Armorstand §7aus der Datenbank entfernen um ihn zerstören zu können.");
            }
        }
    }

}
