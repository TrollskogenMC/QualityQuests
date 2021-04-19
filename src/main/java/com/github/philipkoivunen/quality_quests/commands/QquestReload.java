package com.github.philipkoivunen.quality_quests.commands;

import se.hornta.commando.ICommandHandler;
import se.hornta.versioned_config.ConfigurationException;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.ConfigConstants;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import org.bukkit.command.CommandSender;
import se.hornta.messenger.MessageManager;
import se.hornta.messenger.MessengerException;
import se.hornta.messenger.Translation;

public class QquestReload implements ICommandHandler {
    public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
        try {
            QualityQuestsPlugin.getInstance().getConfiguration().reload();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            MessageManager.sendMessage(commandSender, MessageConstants.CONFIGURATION_RELOAD_FAILURE);
        }

       QualityQuestsPlugin.getInstance().getStorageApi().fetchAllQuests();

        Translation translation = null;
        try {
            translation = QualityQuestsPlugin.getInstance().getTranslations().createTranslation(QualityQuestsPlugin.getInstance().getConfiguration().get(ConfigConstants.LANGUAGE));
        } catch (MessengerException e) {
            e.printStackTrace();
        }
        MessageManager.getInstance().setTranslation(translation);
        MessageManager.sendMessage(commandSender, MessageConstants.CONFIGURATION_RELOAD_SUCCESS);
        QualityQuestsPlugin.getInstance().getServer().getScheduler().runTask(QualityQuestsPlugin.getInstance(), () -> QualityQuestsPlugin.getInstance().getOngoingQuestManager().loadAllOngoingQuests());
    }
}