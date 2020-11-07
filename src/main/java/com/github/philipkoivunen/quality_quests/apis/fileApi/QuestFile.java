package com.github.philipkoivunen.quality_quests.apis.fileApi;

import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/** Handles data surrounding Qeusts */
public class QuestFile {
    private static final String QUEST_DIRECTORY = "quests";
    private static final String QUEST_ID = "id";
    private static final String GOAL_OBJECT = "goal";
    private static final String GOAL_TYPE = "type"; //kill custom or break
    private static final String GOAL_MIN_PARTICIPATION = "min_participation"; //optional automatically set to complete participation
    private static final String GOAL_COMPLETE_PARTICIPATION = "complete_participation";
    private static final String TYPE = "type";
    private File directory;

    public QuestFile(Plugin plugin) {
        directory = new File(plugin.getDataFolder(), QUEST_DIRECTORY);
    }

    public void updateQuest(Quest quest) {
        File questFile = new File(directory, quest.getQuestId() + ".yml");
        CompletableFuture.supplyAsync(() -> {
            YamlConfiguration yaml = new YamlConfiguration();

            Map<String, Object> goalList = new LinkedHashMap<>();
            goalList.put(GOAL_TYPE, quest.getGoalType());
            if(quest.getMinParticipation() > 0) goalList.put(GOAL_MIN_PARTICIPATION, quest.getMinParticipation());
            else goalList.put(GOAL_COMPLETE_PARTICIPATION, quest.getCompeteParticipation());


            yaml.set(QUEST_ID, quest.getQuestId());
            yaml.set(GOAL_OBJECT, goalList);
            yaml.set(TYPE, quest.getType());

            try{
                yaml.save(questFile);
            }catch (IOException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                return false;
            }

            return true;
        });
    }

    public void deleteQuest(Quest quest) {
        File questFile = new File(directory, quest.getQuestId() + ".yml");

        CompletableFuture.supplyAsync(() -> {
            boolean success = false;
            try {
                Files.delete(questFile.toPath());
                success = true;
            } catch (NoSuchFileException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.WARNING, "Failed to delete quest file. File `" + questFile.getName() + "` wasn't found.", ex);
            } catch (DirectoryNotEmptyException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to delete quest file. Expected a file but tried to delete a folder", ex);
            } catch (IOException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, ex.getMessage());
            }

        return success;
        });
    }

    public void fetchQuest(Quest quest) {
        File questFile = new File(directory, quest.getQuestId() + ".yml");
        CompletableFuture.supplyAsync(() -> {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(questFile);
        List<Map<String, Object>> goalEntries = (List<Map<String, Object>>)yaml.getList(GOAL_OBJECT);

            try{
                yaml.save(questFile);
            }catch (IOException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                return false;
            }
            return true;
        });
    }

}
