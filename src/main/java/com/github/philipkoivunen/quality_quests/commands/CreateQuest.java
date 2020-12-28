package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CreateQuest implements ICommandHandler {
    private final StorageApi storageApi;
    private final Quests quests;

    public CreateQuest(StorageApi storageApi, Quests quests) {
        this.storageApi = storageApi;
        this.quests = quests;
    }

    public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
        Quest quest = new Quest();
        quest.setType("quest");
        quest.setQuestId(UUID.randomUUID());
        quest.setQuestName(strings[0]);
        quest.setGoalType(strings[1]);
        quest.setCompleteParticipation(Integer.parseInt(strings[2]));
        quest.setMinParticipationInteger(Integer.parseInt(strings[2]));

        this.quests.addQuest(quest);
        this.storageApi.updateQuest(quest);
        MessageManager.sendMessage(commandSender, MessageConstants.CREATE_QUEST_SUCCESS);
    }
}
