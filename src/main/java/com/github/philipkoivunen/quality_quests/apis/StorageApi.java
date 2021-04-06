package com.github.philipkoivunen.quality_quests.apis;

import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.QuestPlayer;

import java.util.List;

public abstract class StorageApi {
    public abstract void updateQuest(Quest quest);
    public abstract void fetchAllQuests();
}
