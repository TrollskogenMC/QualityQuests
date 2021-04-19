package com.github.philipkoivunen.quality_quests.commands;

import se.hornta.commando.ICommandHandler;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuests;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class QquestsDeActivate implements ICommandHandler {
    private final OngoingQuestManager ongoingQuestManager;
    private final Quests quests;
    private final OngoingQuests ongoingQuests;
    public QquestsDeActivate(OngoingQuestManager ongoingQuestManager, Quests quests, OngoingQuests ongoingQuests) {
        this.ongoingQuestManager = ongoingQuestManager;
        this.quests = quests;
        this.ongoingQuests = ongoingQuests;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Quest quest = quests.getQuestByUUID(UUID.fromString(strings[0]));
        Player player = (Player)commandSender;
        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<OngoingQuest> activeOngoingQuests = ongoingQuests.getPlayersDeactivatedQuests(user.getId());
        OngoingQuest foundOngoingQuest = null;

        for(OngoingQuest ongoingQuest : activeOngoingQuests ) {
            if(ongoingQuest.questId == quest.questId) {
                foundOngoingQuest = ongoingQuest;
                foundOngoingQuest.setIsActive(false);
                break;
            }
        }

        if(foundOngoingQuest == null) {
            this.ongoingQuestManager.postOngoingQuest(user, foundOngoingQuest);
        } else {
            this.ongoingQuestManager.postOngoingQuest(user, new OngoingQuest(0, user.getId(), quest.questId, 0, false, false, quest.questName, Instant.now(), null));
        }
    }
}
