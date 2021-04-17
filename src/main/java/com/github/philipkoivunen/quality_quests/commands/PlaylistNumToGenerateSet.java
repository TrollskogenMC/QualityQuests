package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import org.bukkit.command.CommandSender;

public class PlaylistNumToGenerateSet implements ICommandHandler {
    private static StorageApi storageApi;
    private static Playlists playlists;

    public PlaylistNumToGenerateSet(StorageApi storageApi, Playlists playlists) {
        this.playlists = playlists;
        this.storageApi = storageApi;
    }

    @Override
    public void handle(CommandSender commandSender, String[] strings, int i) {
        Playlist playlist = this.playlists.getPlayListByName(strings[0]);

        playlist.setAmountToGenerate(Integer.parseInt(strings[1]));
        playlists.addPlayList(playlist);
        this.storageApi.updatePlaylist(playlist);
        MessageManager.sendMessage(commandSender, MessageConstants.UPDATE_QUEST_SUCCESS);
    }
}
