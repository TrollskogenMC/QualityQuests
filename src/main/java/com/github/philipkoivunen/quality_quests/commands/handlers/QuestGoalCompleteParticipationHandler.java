package com.github.philipkoivunen.quality_quests.commands.handlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;

import java.util.Set;

public class QuestGoalCompleteParticipationHandler implements IArgumentHandler {
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
