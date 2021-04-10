package com.github.philipkoivunen.quality_quests.objects;

import java.time.Instant;
import java.util.UUID;

public class OngoingQuest {
    public int id;
    public int userId;
    public UUID questId;
    public int participation;
    public boolean isActive;
    public boolean isComplete;
    public String name;
    public Instant lastInteractedWidth;
    public Instant activatedOn;


    public OngoingQuest(int id, int userId, UUID questId, int participation, boolean isActive, Boolean isComplete, String name, Instant activatedOn) {
        this.id = id;
        this.userId = userId;
        this.questId = questId;
        this.participation = participation;
        this.isActive = isActive;
        this.isComplete = isComplete;
        this.name = name;
        this.activatedOn = activatedOn;
    }

    public void SetIsComplete(Boolean isComplete) { this.isComplete = isComplete; }

    public void SetParticipation(int participation) {
        this.participation = participation;
    }

    public void setIsActive(boolean b) { this.isActive = b; }
}
