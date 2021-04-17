package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import com.github.philipkoivunen.quality_quests.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class QquestPlaylistActivateRandom implements ICommandHandler {
    private final Playlists playlists;
    private final StorageApi storageApi;
    private final Quests quests;
    private final OngoingQuests ongoingQuests;
    private final OngoingQuestManager ongoingQuestManager;

    public QquestPlaylistActivateRandom(StorageApi storageApi, Playlists playlists, Quests quests, OngoingQuests ongoingQuests, OngoingQuestManager ongoingQuestManager) {
        this.playlists = playlists;
        this.storageApi = storageApi;
        this.quests = quests;
        this.ongoingQuests = ongoingQuests;
        this.ongoingQuestManager = ongoingQuestManager;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Playlist playlist = playlists.getPlayListByUUID(UUID.fromString(strings[0]));
        Quest quest = this.quests.getQuestByUUID(playlist.getRandomQuestId());
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

        Instant todaysDate = Instant.now();
        Instant expirationDate = playlist.daysToComplete != null ? todaysDate.plus(playlist.daysToComplete, ChronoUnit.DAYS) : null;

        if(foundOngoingQuest != null) {
            this.ongoingQuestManager.postOngoingQuest(user, foundOngoingQuest);
            MessageManager.setValue("progress_current", foundOngoingQuest.participation);
        } else {
            this.ongoingQuestManager.postOngoingQuest(user, new OngoingQuest(0, user.getId(), quest.questId, 0, true, false, quest.questName, todaysDate, playlist.daysToComplete != null ? expirationDate : null));
            MessageManager.setValue("progress_current", 0);
        }

        MessageManager.setValue("quest_name", quest.questName);
        MessageManager.setValue("progress_max", quest.minParticipation);
        if(quest.goalType == QuestTypeConstants.BREAK_BLOCK.toString().toLowerCase()) MessageManager.setValue("goal", quest.blockToDestroy);
        else if(quest.goalType == QuestTypeConstants.KILL.toString().toLowerCase()) MessageManager.setValue("goal", quest.mobToKill);

        MessageManager.sendMessage(commandSender, MessageConstants.START_QUEST_SUCCESS);
    }
}
