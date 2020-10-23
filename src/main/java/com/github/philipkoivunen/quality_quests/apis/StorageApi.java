package com.github.philipkoivunen.quality_quests.apis;

public abstract class StorageApi {
    abstract void UpdateQuest(String QuestId);
    abstract void DeleteQuest(String QuestId);
    abstract void FetchQuest(String QuestId);
    abstract void FetchPlayer(String Uuid);
    abstract void UpdatePlayer(String Uuid);
    abstract void UpdateOngoingQuest(String QuestId);
    abstract void FetchOngoingQuest(String QuestId);
    abstract void DeleteOngoingQuest(String QuestId);
}
