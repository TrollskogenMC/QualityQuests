package com.github.philipkoivunen.quality_quests.apis.fileApi;

import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import org.bukkit.plugin.Plugin;

public class FileApi extends StorageApi {
    QuestFile questFile;
    PlaylistFile playlistFile;
    public FileApi(Plugin plugin) {
        questFile = new QuestFile(plugin);
        playlistFile= new PlaylistFile(plugin);
    }

    @Override
    public void updateQuest(Quest quest) {
        questFile.updateQuest(quest);
    }

    @Override
    public void fetchAllQuests() { questFile.fetchAllQuests(); }

    @Override
    public void fetchAllPlaylists() {
        this.playlistFile.fetchAllPlaylists();
    }

    @Override
    public void updatePlaylist(Playlist playlist) {
        this.playlistFile.updatePlaylist(playlist);
    }
}
