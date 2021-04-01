package com.github.philipkoivunen.quality_quests.events;

import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerLogin implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UserObject user = TrollskogenCorePlugin.getUser(event.getPlayer());
        QualityQuestsPlugin.getInstance().getQuestProgressionManager().onLoginEvent(user.getPlayer());
    }
}
