package com.plutus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MaceListenerV3 implements Listener {
    private final PlutusMacePluginV3 plugin;
    private final AbilityManagerV3 manager;
    private boolean isInternal = false;

    public MaceListenerV3(PlutusMacePluginV3 plugin, AbilityManagerV3 manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                manager.sendPack(e.getPlayer());
            }
        }.runTaskLater(plugin, 40L);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        if (plugin.isPlutusMace(e.getPlayer().getInventory().getItemInMainHand())) {
            e.setCancelled(true);
            manager.handleAbility(e.getPlayer(), e.getPlayer().isSneaking());
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player a && plugin.isPlutusMace(a.getInventory().getItemInMainHand())) {
            manager.handleBloodToll(a);
        }
    }

    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        if (!isInternal && e.getEntity() instanceof Player v) {
            isInternal = true;
            try {
                manager.handleDamageSharing(v, e.getFinalDamage());
            } finally {
                isInternal = false;
            }
        }
    }
}
