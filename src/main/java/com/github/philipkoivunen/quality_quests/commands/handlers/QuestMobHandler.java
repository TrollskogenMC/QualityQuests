package com.github.philipkoivunen.quality_quests.commands.handlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.constants.MobConstants;
import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestMobHandler implements IArgumentHandler {
    @Override
    public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
        return Arrays.stream(MobConstants.values())
                .map(MobConstants::name)
                .map(String::toLowerCase)
                .filter(state -> state.startsWith(argument.toLowerCase()))
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
