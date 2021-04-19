package com.github.philipkoivunen.quality_quests.commands.handlers;

import se.hornta.commando.ValidationResult;
import se.hornta.commando.completers.IArgumentHandler;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import org.bukkit.command.CommandSender;
import se.hornta.messenger.MessageManager;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestGoalTypeHandler implements IArgumentHandler {
    @Override
    public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
        return Arrays.stream(QuestTypeConstants.values())
                .map(QuestTypeConstants::name)
                .map(String::toLowerCase)
                .filter(state -> state.startsWith(argument.toLowerCase(Locale.ENGLISH)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean test(Set<String> items, String argument) {
        return items.contains(argument.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void whenInvalid(ValidationResult result) {
        MessageManager.sendMessage(result.getCommandSender(), MessageConstants.CREATE_QUEST_FAILED_PARAM);
    }
}
