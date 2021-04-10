package com.github.philipkoivunen.quality_quests.helpers;

import com.github.philipkoivunen.quality_quests.apis.fileApi.PlaylistFile;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YamlPlaylistToPlaylist {
    private static final String QUEST_IDS = "questIds";
    private static final String ID = "id";
    private static final String VERSION = "version";
    private static final String PLAYLIST_NAME = "name";

    public static Playlist Convert(YamlConfiguration yamlData) {
        Playlist playlist = new Playlist();

        List<String> questIdsStringList= yamlData.getStringList(QUEST_IDS);
        List<UUID> questIds = new ArrayList<>();

        for(String qId : questIdsStringList) {
            questIds.add(UUID.fromString(qId));
        }

        playlist.setPlayListName(yamlData.getString(PLAYLIST_NAME));
        playlist.setQuestIds(questIds);
        playlist.setVersionNumber(yamlData.getString(VERSION));
        playlist.setId(UUID.fromString(yamlData.getString(ID)));
        return playlist;
    }
}
