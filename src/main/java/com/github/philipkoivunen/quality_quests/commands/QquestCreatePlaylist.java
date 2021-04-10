package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import org.bukkit.command.CommandSender;

public class QquestCreatePlaylist implements ICommandHandler {
    private final Playlists playlists;
    private final StorageApi storageApi;

    public QquestCreatePlaylist(StorageApi storageApi, Playlists playlists) {
        this.playlists = playlists;
        this.storageApi = storageApi;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Playlist playlist = new Playlist();
        playlist.setPlayListName(strings[0]);

        playlists.addPlayList(playlist);
        this.storageApi.updatePlaylist(playlist);
        MessageManager.sendMessage(commandSender, MessageConstants.CREATE_PLAYLIST_SUCCESS);
    }
}
