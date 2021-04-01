package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;

public class QquestsSetBlock implements ICommandHandler {
    private QualityQuestsPlugin pluginInstance;
    private Quests quests;
    private StorageApi storageApi;
    public QquestsSetBlock(QualityQuestsPlugin plugin, Quests questsInstance, StorageApi storageApiInstance) {
        this.pluginInstance = plugin;
        this.quests = questsInstance;
        this.storageApi = storageApiInstance;
    }
    @Override
    public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
        String questName = strings[0];
        String blockName = strings[1];

        Quest quest = quests.getQuestByName(questName);
        if(quest != null) {
            quest.setBlockToDestroy(blockName);
            this.storageApi.updateQuest(quest);
            MessageManager.sendMessage(commandSender, MessageConstants.UPDATE_QUEST_SUCCESS);

        } else {
            MessageManager.sendMessage(commandSender, MessageConstants.UPDATE_QUEST_ERROR);
        }
    }
}
