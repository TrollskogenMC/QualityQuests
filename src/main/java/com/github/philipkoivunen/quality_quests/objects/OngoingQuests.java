package com.github.philipkoivunen.quality_quests.objects;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OngoingQuests {
    private List<OngoingQuest> ongoingQuests;

    public OngoingQuests() {
        this.ongoingQuests = new ArrayList<>();
    }

    public void addOngoingQuest(OngoingQuest ongoingQuest) {
        Boolean hasFound = false;

        if(this.ongoingQuests.size() < 1) {
            this.ongoingQuests.add(ongoingQuest);
        } else {
            for (OngoingQuest o :this. ongoingQuests) {
                if(o.id == ongoingQuest.id) {
                    hasFound = true;
                } else if( o.questId == ongoingQuest.questId)
                    hasFound = true;
            }
            if(!hasFound) {
                this.ongoingQuests.add(ongoingQuest);
            } else {
                deleteOngoingQuest(ongoingQuest.id);
                this.ongoingQuests.add(ongoingQuest);
            }
        }
    }

    public List<OngoingQuest> getPlayersOngoingQuests(int playerId) {
        List<OngoingQuest> newOngoingQuestList = new ArrayList<>();
        for(int i = 0; i < this.ongoingQuests.size(); i++) {
            OngoingQuest o = this.ongoingQuests.get(i);
            if(o.userId == playerId) {
                newOngoingQuestList.add(o);
            }
        }

        return newOngoingQuestList;
    }

    public List<OngoingQuest> getPlayersActiveOngoingQuests(int playerId) {
        List<OngoingQuest> newOngoingQuestList = null;
        for(int i = 0; i < this.ongoingQuests.size(); i++) {
            OngoingQuest o = this.ongoingQuests.get(i);
            if(o.userId == playerId && o.isActive) {
                newOngoingQuestList.add(o);
            }
        }

        return newOngoingQuestList;
    }

    public List<OngoingQuest> getPlayersActiveOngoingQuestsByQuestId(int playerId, UUID questId) {
        List<OngoingQuest> activeOngoingQuestList = new ArrayList<>();
        for(int i = 0; i < this.ongoingQuests.size(); i++) {
            OngoingQuest o = this.ongoingQuests.get(i);
            if(o.userId == playerId && o.isActive && o.questId.equals(questId) ) {
                activeOngoingQuestList.add(o);
            }
        }

        return activeOngoingQuestList;
    }

    public List<OngoingQuest> getPlayersActiveExpiringOngoingQuestsByQuestId(int playerId, UUID questId) {
        List<OngoingQuest> activeOngoingQuestList = new ArrayList<>();
        for(int i = 0; i < this.ongoingQuests.size(); i++) {
            OngoingQuest o = this.ongoingQuests.get(i);
            if(o.userId == playerId && o.isActive && o.questId.equals(questId) && o.expiresOn != null ) {
                activeOngoingQuestList.add(o);
            }
        }

        return activeOngoingQuestList;
    }

    public List<OngoingQuest> getPlayersDeactivatedQuests(int playerId) {
        List<OngoingQuest> newOngoingQuestList = new ArrayList<>();
        for(int i = 0; i < this.ongoingQuests.size(); i++) {
            OngoingQuest o = this.ongoingQuests.get(i);
            if(o.userId == playerId && !o.isActive) {
                newOngoingQuestList.add(o);
            }
        }

        return newOngoingQuestList;
    }

    public List<OngoingQuest> getPlayersActiveExpiredQuests(int playerId) {
        List<OngoingQuest> newOngoingQuestList = new ArrayList<>();
        for(int i = 0; i < this.ongoingQuests.size(); i++) {
            OngoingQuest o = this.ongoingQuests.get(i);
            if(o.expiresOn != null) {
                if (o.userId == playerId && o.isActive && o.expiresOn.isAfter(Instant.now())) {
                    newOngoingQuestList.add(o);
                }
            }
        }

        return newOngoingQuestList;
    }

    public List<OngoingQuest> getOngoingQuests() {
        return this.ongoingQuests;
    }

    public void setList(List<OngoingQuest> data) {
        this.ongoingQuests = data;
    }

    public void deleteOngoingQuest(Integer id) {
        List<OngoingQuest> newOngoingQuestList = new ArrayList<>();
        for(int i = 0; i < this.ongoingQuests.size(); i++) {
            OngoingQuest o = this.ongoingQuests.get(i);
            if(o.id != id) newOngoingQuestList.add(o);
        }

        setList(newOngoingQuestList);
    }

    public void clear() {
        this.ongoingQuests.clear();
    }
}
