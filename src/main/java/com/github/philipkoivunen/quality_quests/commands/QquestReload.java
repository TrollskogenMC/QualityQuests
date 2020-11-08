package com.github.philipkoivunen.quality_quests.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.Translation;
import com.github.hornta.versioned_config.ConfigurationException;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.ConfigConstants;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import org.bukkit.command.CommandSender;

public class QquestReload implements ICommandHandler {
    public void handle(CommandSender commandSender, String[] strings, int typedArgs) {
        try {
            QualityQuestsPlugin.getInstance().getConfiguration().reload();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            MessageManager.sendMessage(commandSender, MessageConstants.CONFIGURATION_RELOAD_FAILURE);
        }
        Translation translation = QualityQuestsPlugin.getInstance().getTranslations().createTranslation(QualityQuestsPlugin.getInstance().getConfiguration().get(ConfigConstants.LANGUAGE));
        MessageManager.getInstance().setTranslation(translation);
        MessageManager.sendMessage(commandSender, MessageConstants.CONFIGURATION_RELOAD_SUCCESS);
    }
}