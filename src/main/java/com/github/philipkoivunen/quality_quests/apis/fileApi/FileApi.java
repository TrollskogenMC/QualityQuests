package com.github.philipkoivunen.quality_quests.apis.fileApi;

import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.QuestPlayer;
import org.bukkit.plugin.Plugin;

public class FileApi extends StorageApi {
    QuestFile questFile;

    public FileApi(Plugin plugin) {
        questFile = new QuestFile(plugin);
    }

    @Override
    public void updateQuest(Quest quest) {
        questFile.updateQuest(quest);
    }

    @Override
    public void deleteQuest(Quest quest) {
        questFile.deleteQuest(quest);
    }

    @Override
    public void fetchQuest(Quest quest) {
        questFile.fetchQuest(quest);
    }

    @Override
    public void fetchPlayer(QuestPlayer player) {

    }

    @Override
    public void updatePlayer(QuestPlayer player) {

    }

    @Override
    public void updateOngoingQuest(OngoingQuest ongoingQuest) {

    }

    @Override
    public void fetchOngoingQuest(OngoingQuest ongoingQuest) {

    }

    @Override
    public void deleteOngoingQuest(OngoingQuest ongoingQuest) {

    }
}
