package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;

public class QquestsSetMob implements ICommandHandler {
    private QualityQuestsPlugin pluginInstance;
    private Quests quests;
    private StorageApi storageApi;
    public  QquestsSetMob() {
        this.pluginInstance = QualityQuestsPlugin.getInstance();
        this.quests = this.pluginInstance.getQuests();
        this.storageApi = this.pluginInstance.getStorageApi();
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        String questName = strings[0];
        String mobName = strings[1];

        Quest quest = quests.getQuestByName(questName);
        if(quest != null) {
            quest.setMobToKill(mobName);
            this.storageApi.updateQuest(quest);
        } else {
            //TODO: Log error
        }
    }
}
