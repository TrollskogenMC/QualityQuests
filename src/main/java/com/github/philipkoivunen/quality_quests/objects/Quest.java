package com.github.philipkoivunen.quality_quests.objects;

import org.bukkit.entity.Mob;

import java.util.UUID;

public class Quest {
    public UUID questId;
    public String type;
    public Integer minParticipation;
    public Integer completeParticipation;
    public String goalType;
    public String questName;
    public Mob mobToKill;

    public void setQuestId(UUID questId) { this.questId = questId; }
    public void setQuestName(String name) { this.questName = name; }
    public void setType(String type) { this.type = type; }
    public void setMinParticipationInteger(Integer minParticipation) { this.minParticipation = minParticipation; }
    public void setCompleteParticipation(Integer completeParticipation) { this.completeParticipation = completeParticipation; }
    public void setGoalType(String goalType) { this.goalType = goalType;}
    public void setMobToKill(Mob mob) { this.mobToKill = mob;}
}
