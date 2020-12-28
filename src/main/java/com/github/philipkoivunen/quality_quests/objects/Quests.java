package com.github.philipkoivunen.quality_quests.objects;

import java.util.List;

public class Quests {
    private List<Quest> quests;

    public void addQuest(Quest quest) {
        if(quests.indexOf(quest) > 0) quests.add(quest);
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setList(List<Quest> data) {
        quests = data;
    }
}
