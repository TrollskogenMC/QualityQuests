package com.github.philipkoivunen.quality_quests.commands.handlers;

import se.hornta.commando.ValidationResult;
import se.hornta.commando.completers.IArgumentHandler;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;
import se.hornta.messenger.MessageManager;

import java.util.*;
import java.util.stream.Collectors;

public class QuestHandler implements IArgumentHandler {
    private QualityQuestsPlugin pluginInstanse;
    private Quests quests;
    public QuestHandler() {
        this.pluginInstanse = QualityQuestsPlugin.getInstance();
        this.quests = pluginInstanse.getQuests();
    }

    @Override
    public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
        return this.quests.getQuests()
                .stream()
                .filter(quest -> quest.questName.toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
                .map(Quest::getQuestName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean test(Set<String> items, String argument) {
        return items.contains(argument.toLowerCase());
    }

    @Override
    public void whenInvalid(ValidationResult result) {
        MessageManager.sendMessage(result.getCommandSender(), MessageConstants.CREATE_QUEST_FAILED_PARAM);
    }
}
