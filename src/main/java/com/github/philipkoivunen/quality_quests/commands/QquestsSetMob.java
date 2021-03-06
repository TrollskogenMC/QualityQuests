package com.github.philipkoivunen.quality_quests.commands;

import se.hornta.commando.ICommandHandler;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;
import se.hornta.messenger.MessageManager;

public class QquestsSetMob implements ICommandHandler {
    private QualityQuestsPlugin pluginInstance;
    private Quests quests;
    private StorageApi storageApi;
    public  QquestsSetMob(QualityQuestsPlugin plugin, Quests questsInstance, StorageApi storageApiInstance) {
        this.pluginInstance = plugin;
        this.quests = questsInstance;
        this.storageApi = storageApiInstance;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        String questName = strings[0];
        String mobName = strings[1];

        Quest quest = quests.getQuestByName(questName);
        if(quest != null) {
            quest.setMobToKill(mobName);
            this.storageApi.updateQuest(quest);
            MessageManager.sendMessage(commandSender, MessageConstants.UPDATE_QUEST_SUCCESS);

        } else {
            MessageManager.sendMessage(commandSender, MessageConstants.UPDATE_QUEST_ERROR);
        }
    }
}
