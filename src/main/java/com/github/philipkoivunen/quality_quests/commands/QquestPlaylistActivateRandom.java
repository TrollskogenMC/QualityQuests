package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import com.github.philipkoivunen.quality_quests.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
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

        if(foundOngoingQuest != null) {
            this.ongoingQuestManager.postOngoingQuest(user, foundOngoingQuest);
        } else {
            this.ongoingQuestManager.postOngoingQuest(user, new OngoingQuest(0, user.getId(), quest.questId, 0, true, false, quest.questName, Instant.now()));
        }
    }
}
