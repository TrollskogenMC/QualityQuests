package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;


public class QquestPlaylistAddQuest implements ICommandHandler {
    private final Playlists playlists;
    private final StorageApi storageApi;
    private final Quests quests;
    public QquestPlaylistAddQuest(StorageApi storageApi, Playlists playlists, Quests quests) {
        this.playlists = playlists;
        this.storageApi = storageApi;
        this.quests = quests;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        String questName = strings[1];
        Quest quest = quests.getQuestByName(questName);

        String playListName = strings[0];
        Playlist playlist = playlists.getPlayListByName(playListName);

        //Playlist playlist = playlists.getPlayListByUUID(UUID.fromString(strings[0]));
        playlist.addQuestId(quest.questId);
        this.playlists.delete(playlist);
        this.playlists.addPlayList(playlist);

        this.storageApi.updatePlaylist(playlist);
    }
}
