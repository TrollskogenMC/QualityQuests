package com.github.philipkoivunen.quality_quests.commands;

import se.hornta.commando.ICommandHandler;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.hornta.messenger.MessageManager;

import java.util.List;

public class QquestsList implements ICommandHandler {
    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Quests quests = QualityQuestsPlugin.getInstance().getQuests();
        Player player = (Player)commandSender;
        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<OngoingQuest> ongoingQuests = QualityQuestsPlugin.getInstance().getOngoingQuests().getPlayersActiveNotCompleteOngoingQuests(user.getId());

        if(quests.getQuests().size() < 1 || ongoingQuests.size() < 1) {
            MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_EMPTY);
        } else {
            MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUESTS_TITLE);
            for(Quest quest: quests.getQuests()) {
                OngoingQuest ongoingQuest = null;

                for(OngoingQuest o : ongoingQuests) {
                    if(o.questId.equals(quest.questId)) {
                        ongoingQuest = o;
                        break;
                    }
                }

                if(ongoingQuest != null) {
                    MessageManager.setValue("quest_name", quest.questName);
                    if (quest.goalType.equals("kill")) {
                        MessageManager.setValue("progress_current", ongoingQuest.participation);
                        MessageManager.setValue("progress_max", quest.minParticipation);
                        MessageManager.setValue("mob", quest.mobToKill);
                        MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_KILL);
                    } else if (quest.goalType.equals("break_block")) {
                        MessageManager.setValue("progress_current", ongoingQuest.participation);
                        MessageManager.setValue("progress_max", quest.minParticipation);
                        MessageManager.setValue("block", quest.blockToDestroy);
                        MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_BREAK);
                    } else {
                        MessageManager.setValue("progress_current", ongoingQuest.participation);
                        MessageManager.setValue("progress_max", quest.minParticipation);
                        MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_CUSTOM);
                    }
                }
            }
        }
    }
}
