package com.github.philipkoivunen.quality_quests.objects;

import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quests {
    private List<Quest> quests;

    public Quests() {
        quests = new ArrayList<>();
    }

    public void addQuest(Quest quest) {
        if(quests.size() < 1) {
            quests.add(quest);
        } else {
            Boolean hasFound = false;

            for (Quest q : quests) {
                if (q.questId == quest.questId) hasFound = true;
            }
            if (!hasFound) {
                quests.add(quest);
            } else {
                deleteQuest(quest);
                quests.add(quest);
            }
        }
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void deleteQuest(Quest quest) {
        List<Quest> newQuestList = null;
        for(int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            if(q.questId != quest.questId) newQuestList.add(q);
        }

        setList(newQuestList);
    }

    public void setList(List<Quest> data) {
        quests = data;
    }

    public Quest getQuestByName(String name) {
        Quest quest = null;
        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            if (q.questName.toLowerCase().equals(name)) quest = q;
        }
        return quest;
    }

    public List<Quest> getQuestsByType(String type) {
        List<Quest> foundQuests = new ArrayList<>();
        for (int i = 0; i < this.quests.size(); i++) {
            Quest q = this.quests.get(i);
            if (q.goalType.toLowerCase().equals(type.toLowerCase())) foundQuests.add(q);
        }
        return foundQuests;
    }

    public Quest getQuestByUUID(UUID uuid) {
        Quest quest = null;
        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            if (q.questId.equals(uuid)) quest = q;
        }
        return  quest;
    }

    public List<Quest> getQuestsByMobToKill(EntityType entityType) {
        List<Quest> foundQuests = new ArrayList<>();
        for (int i = 0; i < this.quests.size(); i++) {
            Quest q = this.quests.get(i);
            if (q.mobToKill != null && q.goalType.toLowerCase().equals(QuestTypeConstants.KILL.toString().toLowerCase()) && entityType.name().toLowerCase().equals(q.mobToKill.toLowerCase())) foundQuests.add(q);
        }
        return foundQuests;
    }

    public List<Quest> getQuestsByBlockToBreak(Material material) {
        List<Quest> foundQuests = new ArrayList<>();
        for (int i = 0; i < this.quests.size(); i++) {
            String questTypeConstant = QuestTypeConstants.BREAK_BLOCK.toString().toLowerCase();

            Quest q = this.quests.get(i);
            if (q.goalType.equals(questTypeConstant) && q.blockToDestroy != null) {
                String materialName = material.name().toLowerCase();
                String goalType = q.blockToDestroy.toLowerCase();
                if (materialName.equals(goalType)) foundQuests.add(q);
            }
        }
        return foundQuests;
    }
}
