package com.github.philipkoivunen.quality_quests.objects;

import java.util.UUID;

public class Quest {
    public UUID questId;
    public String type;
    public Integer minParticipation;
    public Integer completeParticipation;
    public String goalType;

    public void setQuestId(UUID questId) { this.questId = questId; }
    public void setType(String type) { this.type = type; }
    public void setMinParticipationInteger(Integer minParticipation) { this.minParticipation = minParticipation; }
    public void setCompleteParticipation(Integer completeParticipation) { this.completeParticipation = completeParticipation; }
    public void setGoalType(String goalType) { this.goalType = goalType;}
}
