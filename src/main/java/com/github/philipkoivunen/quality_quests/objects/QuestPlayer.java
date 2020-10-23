package com.github.philipkoivunen.quality_quests.objects;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Holds information about quests for a player during runtime
 */
public class QuestPlayer {
    public Integer playerId;
    public UUID playerUUID;
    public Integer completedQuests;

    public void setPlayerId(Player player) {

    }
}
