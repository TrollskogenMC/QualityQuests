package com.github.philipkoivunen.quality_quests.objects;

public class OngoingQuest {
    public int id;
    public int userId;
    public int questId;
    public int participation;
    public boolean isActive;

    public OngoingQuest(int id, int userId, int questId, int participation, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.questId = questId;
        this.participation = participation;
        this.isActive = isActive;
    }
}
