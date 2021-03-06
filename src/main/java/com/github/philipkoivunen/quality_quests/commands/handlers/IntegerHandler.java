package com.github.philipkoivunen.quality_quests.commands.handlers;

import se.hornta.commando.ValidationResult;
import se.hornta.commando.completers.IArgumentHandler;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import se.hornta.messenger.MessageManager;

import java.util.Set;

public class IntegerHandler implements IArgumentHandler {
    @Override
    public boolean test(Set<String> items, String argument) {
        int argumentInt = Integer.parseInt(argument);
        if(argumentInt >= 1) return true;
        return false;
    }

    @Override
    public void whenInvalid(ValidationResult result) {
        MessageManager.sendMessage(result.getCommandSender(), MessageConstants.CREATE_QUEST_FAILED_PARAM);
    }
}
