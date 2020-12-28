package com.github.philipkoivunen.quality_quests.objects;

import java.util.List;

public class Quests {
    private List<Quest> quests;

    public void addQuest(Quest quest) {
        Boolean hasFound = false;
        for(Quest q: quests) {
            if(q.questId == quest.questId) hasFound = true;
        }
        if(!hasFound) {
            quests.add(quest);
        } else {
            deleteQuest(quest);
            quests.add(quest);
        }
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void deleteQuest(Quest quest) {
        List<Quest> newQuestList = null;
        for(int i = 0; i < quests.size(); i++) {
            Quest q = quests.get(i);
            if(q.questId != quest.questId) newQuestList.add(quest);
        }

        setList(newQuestList);
    }

    public void setList(List<Quest> data) {
        quests = data;
    }
}
