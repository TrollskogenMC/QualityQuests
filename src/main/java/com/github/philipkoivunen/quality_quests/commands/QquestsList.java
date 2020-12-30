package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import org.bukkit.command.CommandSender;

import java.util.List;

public class QquestsList implements ICommandHandler {
    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        List<Quest> quests = QualityQuestsPlugin.getInstance().getQuests().getQuests();

        if(quests.size() < 1) {
            MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_EMPTY);
        } else {
            MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUESTS_TITLE);
            for(Quest quest: quests) {
                MessageManager.setValue("quest_name", quest.questName);
                if (quest.goalType.equals("kill")) {
                    //TODO: Get active quest-data and get my progress
                    MessageManager.setValue("progress_current", 0);
                    MessageManager.setValue("progress_max", quest.minParticipation);
                    MessageManager.setValue("mob", quest.mobToKill);
                    MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_KILL);
                } else if (quest.goalType.equals("break_block")) {
                    //TODO: Get active quest-data and get my progress
                    MessageManager.setValue("progress_current", 0);
                    MessageManager.setValue("progress_max", quest.minParticipation);
                    MessageManager.setValue("block", quest.blockToDestroy);
                    MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_BREAK);
                } else {
                    //TODO: Get active quest-data and get my progress
                    MessageManager.setValue("progress_current", 0);
                    MessageManager.setValue("progress_max", quest.minParticipation);
                    MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_CUSTOM);
                }
            }
        }
    }
}
