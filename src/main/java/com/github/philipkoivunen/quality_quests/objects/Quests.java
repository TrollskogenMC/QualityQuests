package com.github.philipkoivunen.quality_quests.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Quests {
    private List<Quest> quests;

    public Quests() {
        quests = new ArrayList<>();
    }

    public void addQuest(Quest quest) {
        Boolean hasFound = false;
        if(quests.size() < 1) {
            quests.add(quest);
        } else {
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
        List<Quest> quests = new ArrayList<>();
        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            if (q.type.toLowerCase().equals(type)) quests.add(q);
        }
        return quests;
    }

    public Quest getQuestByUUID(UUID uuid) {
        Quest quest = null;
        for (int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            if (q.questId.equals(uuid)) quest = q;
        }
        return  quest;
    }
}
