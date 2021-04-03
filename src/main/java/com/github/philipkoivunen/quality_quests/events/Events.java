package com.github.philipkoivunen.quality_quests.events;

import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class Events implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        QualityQuestsPlugin.getInstance().getQuestProgressionManager().onLoginEvent(event.getPlayer());
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();

         QualityQuestsPlugin.getInstance().getQuestProgressionManager().onKillEvent(player, entity.getType());
    }

    @EventHandler
    public void breakBlockEvent(BlockBreakEvent blockBreakEvent) {
        QualityQuestsPlugin.getInstance().getQuestProgressionManager().onBreakBlockEvent(blockBreakEvent.getPlayer(), blockBreakEvent.getBlock());
    }
}
