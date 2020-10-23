package com.github.philipkoivunen.quality_quests.apis;

import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.QuestPlayer;

public abstract class StorageApi {
    public abstract void updateQuest(Quest quest);
    public abstract void deleteQuest(Quest quest);
    public abstract void fetchQuest(Quest quest);
    public abstract void fetchPlayer(QuestPlayer player);
    public abstract void updatePlayer(QuestPlayer player);
    public abstract void updateOngoingQuest(OngoingQuest ongoingQuest);
    public abstract void fetchOngoingQuest(OngoingQuest ongoingQuest);
    public abstract void deleteOngoingQuest(OngoingQuest ongoingQuest);
}
