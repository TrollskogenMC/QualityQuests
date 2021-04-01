package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuests;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class QquestsList implements ICommandHandler {
    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Quests quests = QualityQuestsPlugin.getInstance().getQuests();
        Player player = (Player)commandSender;
        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<OngoingQuest> ongoingQuests = QualityQuestsPlugin.getInstance().getOngoingQuests().getPlayersOngoingQuests(user.getId());

        if(quests.getQuests().size() < 1) {
            MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_EMPTY);
        } else {
            MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUESTS_TITLE);
            for(Quest quest: quests.getQuests()) {
                //Quest quest = quests.getQuestByUUID(quests.questId);
                MessageManager.setValue("quest_name", quest.questName);
                if (quest.goalType.equals("kill")) {
                    //TODO: Get active quest-data and get my progress
                    MessageManager.setValue("progress_current", 0);
                    MessageManager.setValue("progress_max", quest.minParticipation);
                    MessageManager.setValue("mob", quest.mobToKill);
                    //MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_KILL);

                    ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/qquests activate " + quest.questId);

                    TextComponent tc = new TextComponent();
                    tc.addExtra(new ComponentBuilder(MessageManager.getMessage(MessageConstants.LIST_QUEST_KILL)).event(clickEvent).create()[0]);

                    commandSender.spigot().sendMessage(tc);
                } else if (quest.goalType.equals("break_block")) {
                    //TODO: Get active quest-data and get my progress
                    MessageManager.setValue("progress_current", 0);
                    MessageManager.setValue("progress_max", quest.minParticipation);
                    MessageManager.setValue("block", quest.blockToDestroy);
                    //MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_BREAK);

                    ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/qquests activate " + quest.questId);
                    TextComponent tc = new TextComponent();
                    tc.addExtra(new ComponentBuilder(MessageManager.getMessage(MessageConstants.LIST_QUEST_BREAK)).event(clickEvent).create()[0]);

                    commandSender.spigot().sendMessage(tc);
                } else {
                    //TODO: Get active quest-data and get my progress
                    MessageManager.setValue("progress_current", 0);
                    MessageManager.setValue("progress_max", quest.minParticipation);

                    ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/qquests activate " + quest.questId);

                    TextComponent tc = new TextComponent();
                    tc.addExtra(new ComponentBuilder(MessageManager.getMessage(MessageConstants.LIST_QUEST_CUSTOM)).event(clickEvent).create()[0]);
                    //MessageManager.sendMessage(commandSender, MessageConstants.LIST_QUEST_CUSTOM);

                    commandSender.spigot().sendMessage(tc);
                }
            }
        }
    }
}
