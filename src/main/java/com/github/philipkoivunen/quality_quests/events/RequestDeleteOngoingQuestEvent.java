package com.github.philipkoivunen.quality_quests.events;

import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestDeleteOngoingQuestEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final OngoingQuest ongoingQuest;

    public RequestDeleteOngoingQuestEvent(OngoingQuest ongoingQuest) {
        this.ongoingQuest = ongoingQuest;
    }

    public OngoingQuest getOngoingQuest() {
        return ongoingQuest;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
