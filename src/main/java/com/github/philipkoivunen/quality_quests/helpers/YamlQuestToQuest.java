package com.github.philipkoivunen.quality_quests.helpers;

import com.github.philipkoivunen.quality_quests.objects.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

public class YamlQuestToQuest {

    private static final String QUEST_NAME = "name";
    private static final String QUEST_ID = "id";
    private static final String GOAL_OBJECT = "goal";
    private static final String GOAL_TYPE = "type"; //kill custom or break
    private static final String GOAL_MIN_PARTICIPATION = "min_participation";
    private static final String GOAL_MOB = "goal_mob";
    private static final String GOAL_BLOCK = "goal_block";
    private static final String TYPE = "type";
    private static final String COMMANDS_COMPLETE = "commands_complete";


    public static Quest Convert(YamlConfiguration yamlData) {
        Quest quest = new Quest();

        ConfigurationSection goalEntries = yamlData.getConfigurationSection(GOAL_OBJECT);

        quest.setGoalType((String) goalEntries.get(GOAL_TYPE));
        quest.setMinParticipationInteger((Integer) goalEntries.get(GOAL_MIN_PARTICIPATION));
        quest.setMobToKill((String) goalEntries.get(GOAL_MOB));
        quest.setBlockToDestroy((String) goalEntries.get(GOAL_BLOCK));

        quest.setType(yamlData.getString(TYPE));
        quest.setQuestId(UUID.fromString(yamlData.getString(QUEST_ID)));

        quest.setCommands(yamlData.getList(COMMANDS_COMPLETE));
        quest.setQuestName(yamlData.getString(QUEST_NAME));

        return quest;
    }
}
