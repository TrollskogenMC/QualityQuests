package com.github.philipkoivunen.quality_quests.apis.fileApi;

import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.helpers.YamlPlaylistToPlaylist;
import com.github.philipkoivunen.quality_quests.helpers.YamlQuestToQuest;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PlaylistFile {
    private File directory;
    private static final String PLAYLIST_DIRECTORY = "playlists";
    private static final String QUEST_IDS = "questIds";
    private static final String ID = "id";
    private static final String VERSION = "version";
    private static final String PLAYLIST_NAME = "name";
    private static final String DAYS_TO_COMPLETE = "daysToComplete";

    public PlaylistFile(Plugin plugin) {
        directory = new File(plugin.getDataFolder(), PLAYLIST_DIRECTORY);
    }

    public void updatePlaylist(Playlist playlist) {
        File playListFile = new File(directory, playlist.id.toString() + ".yml");
        CompletableFuture.supplyAsync(() -> {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set(VERSION, playlist.versionNumber);
            yaml.set(ID, playlist.id);
            yaml.set(PLAYLIST_NAME, playlist.playListName);
            yaml.set(QUEST_IDS, playlist.questIds);
            yaml.set(DAYS_TO_COMPLETE, playlist.daysToComplete);
            try{
                yaml.save(playListFile);
            }catch (IOException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                return false;
            }
            return  true;
        });
    }

    public void deletePlaylist(Playlist playList) {
        File playlistFile = new File(directory, playList.id.toString() + ".yml");

        CompletableFuture.supplyAsync(() -> {
            Boolean success = false;

            try {
                Files.delete(playlistFile.toPath());
                success = true;
            } catch (NoSuchFileException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.WARNING, "Failed to delete Playlist file. File `" + playlistFile.getName() + "` wasn't found.", ex);
            } catch (DirectoryNotEmptyException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to delete quest file. Expected a file but tried to delete a folder", ex);
            } catch (IOException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, ex.getMessage());
            }
            return success;
        });
    }

    public void fetchAllPlaylists() {
        List<Playlist> playLists = new ArrayList<>();
        CompletableFuture.supplyAsync(() -> {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

                    try {
                        yaml.save(file);
                    } catch (IOException ex) {
                        QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        return false;
                    }
                    playLists.add(YamlPlaylistToPlaylist.Convert(yaml));
                }
            }
            if(playLists.size() > 0) QualityQuestsPlugin.getInstance().getPlayLists().setList(playLists);
            return true;
        });
    }
}
