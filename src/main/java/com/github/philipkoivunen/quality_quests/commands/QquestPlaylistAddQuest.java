package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class QquestPlaylistAddQuest implements ICommandHandler {
    private final Playlists playlists;
    private final StorageApi storageApi;
    public QquestPlaylistAddQuest(StorageApi storageApi, Playlists playlists) {
        this.playlists = playlists;
        this.storageApi = storageApi;
    }
    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Playlist playlist = playlists.getPlayListByUUID(UUID.fromString(strings[0]));
        playlist.addQuestId(UUID.fromString(strings[1]));
        this.playlists.addPlayList(playlist);
    }
}
