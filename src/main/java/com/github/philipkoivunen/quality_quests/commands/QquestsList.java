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

        for(Quest quest: quests) {
            MessageManager.setValue("quest_name", quest.questName);
            MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST);
        }
    }
}
