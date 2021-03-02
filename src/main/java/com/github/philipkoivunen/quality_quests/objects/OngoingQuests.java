package com.github.philipkoivunen.quality_quests.objects;

import java.util.ArrayList;
import java.util.List;

public class OngoingQuests {
    private List<OngoingQuest> ongoingQuests;

    public OngoingQuests() {
        ongoingQuests = new ArrayList<>();
    }

    public void addOngoingQuest(OngoingQuest ongoingQuest) {
        Boolean hasFound = false;
        if(ongoingQuests.size() < 1) {
            ongoingQuests.add(ongoingQuest);
        } else {
            for (OngoingQuest o : ongoingQuests) {
                if(o.id == ongoingQuest.id) {
                    hasFound = true;
                }
            }
            if(!hasFound) {
                ongoingQuests.add(ongoingQuest);
            } else {
                deleteOngoingQuest(ongoingQuest);
                ongoingQuests.add(ongoingQuest);
            }
        }
    }

    public List<OngoingQuest> getPlayersOngoingQuests(int playerId) {
        List<OngoingQuest> newOngoingQuestList = null;
        for(int i = 0; i < ongoingQuests.size(); i++) {
            OngoingQuest o =ongoingQuests.get(i);
            if(o.userId == playerId) {
                newOngoingQuestList.add(o);
            }
        }

        return newOngoingQuestList;
    }

    public List<OngoingQuest> getPlayersActiveOngoingQuests(int playerId) {
        List<OngoingQuest> newOngoingQuestList = null;
        for(int i = 0; i < ongoingQuests.size(); i++) {
            OngoingQuest o =ongoingQuests.get(i);
            if(o.userId == playerId && o.isActive) {
                newOngoingQuestList.add(o);
            }
        }

        return newOngoingQuestList;
    }

    public List<OngoingQuest> getOngoingQuests() {
        return ongoingQuests;
    }

    public void setList(List<OngoingQuest> data) {
        ongoingQuests = data;
    }


    public void deleteOngoingQuest(OngoingQuest ongoingQuest) {
        List<OngoingQuest> newOngoingQuestList = null;
        for(int i = 0; i < ongoingQuests.size(); i++) {
            OngoingQuest o = ongoingQuests.get(i);
            if(o.id != ongoingQuest.id) newOngoingQuestList.add(o);
        }
    }

    public void clear() {
        this.ongoingQuests.clear();
    }
}
