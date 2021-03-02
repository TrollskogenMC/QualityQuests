package com.github.philipkoivunen.quality_quests.events;

import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DeleteOngoingQuestsEvent extends Event {
    public DeleteOngoingQuestsEvent(OngoingQuest ongoingQuest) {
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
