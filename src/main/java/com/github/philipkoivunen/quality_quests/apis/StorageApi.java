package com.github.philipkoivunen.quality_quests.apis;

import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Quest;

public abstract class StorageApi {
    public abstract void updateQuest(Quest quest);
    public abstract void fetchAllQuests();
    public abstract void fetchAllPlaylists();
    public abstract void updatePlaylist(Playlist playlist);
}
