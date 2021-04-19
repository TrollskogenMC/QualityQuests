package com.github.philipkoivunen.quality_quests.commands;

import se.hornta.commando.ICommandHandler;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import org.bukkit.command.CommandSender;
import se.hornta.messenger.MessageManager;

public class PlaylistDaysSet implements ICommandHandler {
    private final Playlists playlists;
    private final StorageApi storageApi;

    public PlaylistDaysSet(StorageApi storageApi, Playlists playlists) {
        this.playlists = playlists;
        this.storageApi = storageApi;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Playlist playlist = this.playlists.getPlayListByName(strings[0]);

        playlist.setDaysToComplete(Integer.parseInt(strings[1]));
        playlists.addPlayList(playlist);
        this.storageApi.updatePlaylist(playlist);
        MessageManager.sendMessage(commandSender, MessageConstants.UPDATE_PLAYLIST_SUCCESS);
    }
}
