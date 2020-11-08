package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CreateQuest implements ICommandHandler {
    private final StorageApi storageApi;

    public CreateQuest(StorageApi storageApi) {
        this.storageApi = storageApi;
    }

    public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
        Quest quest = new Quest();
        quest.setType("quest");
        quest.setQuestId(UUID.randomUUID());
        quest.setGoalType(strings[0]);
        quest.setCompleteParticipation(Integer.parseInt(strings[1]));
        quest.setMinParticipationInteger(Integer.parseInt(strings[1]));

        this.storageApi.updateQuest(quest);
        MessageManager.sendMessage(commandSender, MessageConstants.CREATE_QUEST_SUCCESS);
    }
}
