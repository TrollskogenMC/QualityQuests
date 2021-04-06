package com.github.philipkoivunen.quality_quests.apis.fileApi;

import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.helpers.YamlQuestToQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/** Handles data surrounding Qeusts */
public class QuestFile {
    private static final String QUEST_DIRECTORY = "quests";
    private static final String QUEST_NAME = "name";
    private static final String QUEST_ID = "id";
    private static final String GOAL_OBJECT = "goal";
    private static final String GOAL_TYPE = "type"; //kill custom or break
    private static final String GOAL_MIN_PARTICIPATION = "min_participation"; //optional automatically set to complete participation
    private static final String GOAL_COMPLETE_PARTICIPATION = "complete_participation";
    private static final String GOAL_MOB = "goal_mob";
    private static final String GOAL_BLOCK = "goal_block";
    private static final String TYPE = "type";
    private static final String VERSION = "version";
    private static final String COMMANDS_COMPLETE = "commands_complete";
    private static final String CURRENT_VERSION = "1";

    private File directory;

    public QuestFile(Plugin plugin) {
        directory = new File(plugin.getDataFolder(), QUEST_DIRECTORY);
    }

    public void updateQuest(Quest quest) {
        File questFile = new File(directory, quest.questId.toString() + ".yml");
        CompletableFuture.supplyAsync(() -> {
            YamlConfiguration yaml = new YamlConfiguration();

            Map<String, Object> goalList = new LinkedHashMap<>();

            goalList.put(GOAL_TYPE, quest.goalType);

            if (quest.minParticipation > 0) goalList.put(GOAL_MIN_PARTICIPATION, quest.minParticipation);
            else goalList.put(GOAL_COMPLETE_PARTICIPATION, quest.completeParticipation);

            goalList.put(GOAL_MOB, quest.mobToKill);
            goalList.put(GOAL_BLOCK, quest.blockToDestroy);

            yaml.set(QUEST_ID, quest.questId.toString());
            yaml.set(QUEST_NAME, quest.questName);
            yaml.set(GOAL_OBJECT, goalList);
            yaml.set(TYPE, quest.type);
            yaml.set(VERSION, CURRENT_VERSION);

            // We do not want to edit this field just initiate it
            if(quest.commands == null || quest.commands.size() < 1) {
                yaml.set(COMMANDS_COMPLETE, new ArrayList<String>());
            }

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
        File questFile = new File(directory, quest.questId + ".yml");

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

    public void fetchAllQuests() {
        List<Quest> quests = new ArrayList<>();
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
                    quests.add(YamlQuestToQuest.Convert(yaml));
                }
            }
            if(quests.size() > 0) QualityQuestsPlugin.getInstance().getQuests().setList(quests);
            return true;
        });
    }

    public void fetchQuest(Quest quest) {
        File questFile = new File(directory, quest.questId + ".yml");
        CompletableFuture.supplyAsync(() -> {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(questFile);
        List<Map<String, Object>> goalEntries = (List<Map<String, Object>>)yaml.getList(GOAL_OBJECT);

            try{
                yaml.save(questFile);
            } catch (IOException ex) {
                QualityQuestsPlugin.getInstance().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                return false;
            }
            return true;
        });
    }

}

