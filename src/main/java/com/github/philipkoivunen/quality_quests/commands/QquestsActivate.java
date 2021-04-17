package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuests;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class QquestsActivate implements ICommandHandler {
    private final OngoingQuestManager ongoingQuestManager;
    private final Quests quests;
    private final OngoingQuests ongoingQuests;
    public QquestsActivate(OngoingQuestManager ongoingQuestManager, Quests quests, OngoingQuests ongoingQuests) {
        this.ongoingQuestManager = ongoingQuestManager;
        this.quests = quests;
        this.ongoingQuests = ongoingQuests;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Quest quest = quests.getQuestByUUID(UUID.fromString(strings[0]));
        Player player;

        if(strings.length >= 2) {
            player = Bukkit.getPlayer(strings[1]);
        }else {
            player = (Player)commandSender;
        }
        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<OngoingQuest> activeOngoingQuests = ongoingQuests.getPlayersDeactivatedQuests(user.getId());
        OngoingQuest foundOngoingQuest = null;

        if(activeOngoingQuests != null && activeOngoingQuests.size() > 0) {
        for(OngoingQuest ongoingQuest : activeOngoingQuests ) {
            if(ongoingQuest.questId == quest.questId) {
                foundOngoingQuest = ongoingQuest;
                foundOngoingQuest.setIsActive(true);
                break;
            }
        }
    }

        if(foundOngoingQuest != null) {
            this.ongoingQuestManager.postOngoingQuest(user, foundOngoingQuest);
            MessageManager.setValue("progress_current", foundOngoingQuest.participation);
        } else {
            this.ongoingQuestManager.postOngoingQuest(user, new OngoingQuest(0, user.getId(), quest.questId, 0, true, false, quest.questName, Instant.now(), null));
            MessageManager.setValue("progress_current", 0);
        }

        MessageManager.setValue("progress_max", quest.minParticipation);
        if(quest.goalType == "break_block") MessageManager.setValue("goal", quest.blockToDestroy);
        else if(quest.goalType == "kill") MessageManager.setValue("goal", quest.mobToKill);

        MessageManager.sendMessage(commandSender, MessageConstants.START_QUEST_SUCCESS);
    }
}
