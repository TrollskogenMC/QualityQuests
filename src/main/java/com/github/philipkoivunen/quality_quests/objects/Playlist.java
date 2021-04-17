package com.github.philipkoivunen.quality_quests.objects;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Playlist {
    public String versionNumber;
    public List<UUID> questIds;
    public String playListName;
    public UUID id;
    public Integer daysToComplete;
    public Boolean activateOnFirstLogin;
    public Integer amountToGenerate;

    public Playlist() {
        this.versionNumber = "1";
        this.questIds = new ArrayList<>();
        this.id = UUID.randomUUID();
        this.amountToGenerate = 0;
        this.activateOnFirstLogin = false;
    }

    public void setPlayListName(String name) {
        this.playListName = name;
    }
    public void setQuestIds(List<UUID> newQuestIds) {
        this.questIds = newQuestIds;
    }
    public void setAmountToGenerate(Integer integer) {
        this.amountToGenerate = integer;
    }
    public void setActivateOnFirstLogin(Boolean bool) {
        this.activateOnFirstLogin = bool;
    }

    public void addQuestId(UUID questId) {
        Boolean hasFound = false;
        if(questIds.size() < 1) {
            questIds.add(questId);
        } else {
            for(UUID uuid : questIds) {
                if(questId == uuid) {
                    hasFound = true;
                }
            }
            if(!hasFound) {
                questIds.add(questId);
            }
        }
    }

    public void deleteQuest(UUID questId) {
        List<UUID> newQuestList = new ArrayList<>();
        for(UUID uuid: questIds) {
            if(uuid != questId) {
                newQuestList.add(uuid);
            }
        }

        this.setQuestIds((newQuestList));
    }

    public UUID getRandomQuestId() {
        int questSize = questIds.size();
        int randomNumber = ThreadLocalRandom.current().nextInt(0,questSize);

        return questIds.get(randomNumber);
    }

    public void setVersionNumber(String string) {
        this.versionNumber = string;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }
    public void setDaysToComplete(Integer daysToComplete) {
        this.daysToComplete = daysToComplete;
    }

    public static String getId(Playlist playlist) {
        return  playlist.id.toString();
    }

    public static String getName(Playlist playlist) {
        return playlist.playListName.toString();
    }
}
