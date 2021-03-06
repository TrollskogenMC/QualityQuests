package com.github.philipkoivunen.quality_quests.events;

import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        QualityQuestsPlugin.getInstance().getQuestProgressionManager().onLoginEvent(event.getPlayer());
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();

        if(player != null) QualityQuestsPlugin.getInstance().getQuestProgressionManager().onKillEvent(player, entity.getType());
    }

    @EventHandler
    public void breakBlockEvent(BlockBreakEvent blockBreakEvent) {
        QualityQuestsPlugin.getInstance().getQuestProgressionManager().onBreakBlockEvent(blockBreakEvent.getPlayer(), blockBreakEvent.getBlock());
    }
}
