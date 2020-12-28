package com.github.philipkoivunen.quality_quests.commands.handlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;

import java.util.Set;

public class QuestNameHandler implements IArgumentHandler {
    @Override
    public boolean test(Set<String> items, String argument) {
        if(argument.length() >= 3) return true;
        return false;
    }

    @Override
    public void whenInvalid(ValidationResult result) {
        MessageManager.sendMessage(result.getCommandSender(), MessageConstants.CREATE_QUEST_FAILED_PARAM);
    }
}
