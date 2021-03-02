package com.github.philipkoivunen.quality_quests.events;

import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoadOngoingQuestsEvent extends Event {
    private final OngoingQuestManager ongoingQuestManager;
    private static final  HandlerList HANDLERS = new HandlerList();

    public LoadOngoingQuestsEvent(OngoingQuestManager ongoingQuestManager) {
        this.ongoingQuestManager = ongoingQuestManager;
    }

    public OngoingQuestManager getOngoingQuestManager() {
        return ongoingQuestManager;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
