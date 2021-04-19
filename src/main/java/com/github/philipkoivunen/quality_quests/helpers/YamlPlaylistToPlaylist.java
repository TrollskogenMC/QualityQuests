package com.github.philipkoivunen.quality_quests.helpers;

import com.github.philipkoivunen.quality_quests.apis.fileApi.PlaylistFile;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YamlPlaylistToPlaylist {
    private static final String QUEST_IDS = "questIds";
    private static final String ID = "id";
    private static final String VERSION = "version";
    private static final String PLAYLIST_NAME = "name";
    private static final String DAYS_TO_COMPLETE = "daysToComplete";
    private static final String NUM_TO_GENERATE = "numToGenerate";
    private static final String ACTIVATE_ON_FIRST_LOGIN = "activateOnFirstLogin";

    @NotNull
    public static Playlist Convert(@NotNull YamlConfiguration yamlData) {
        Playlist playlist = new Playlist();

        List<String> questIdsStringList= yamlData.getStringList(QUEST_IDS);
        List<UUID> questIds = new ArrayList<>();

        for(String qId : questIdsStringList) {
            questIds.add(UUID.fromString(qId));
        }

        playlist.setDaysToComplete(yamlData.getInt(DAYS_TO_COMPLETE));
        playlist.setAmountToGenerate(yamlData.getInt(NUM_TO_GENERATE));
        playlist.setPlayListName(yamlData.getString(PLAYLIST_NAME));
        playlist.setQuestIds(questIds);
        playlist.setVersionNumber(yamlData.getString(VERSION));
        playlist.setId(UUID.fromString(yamlData.getString(ID)));
        playlist.setActivateOnFirstLogin(yamlData.getBoolean(ACTIVATE_ON_FIRST_LOGIN));
        return playlist;
    }
}
