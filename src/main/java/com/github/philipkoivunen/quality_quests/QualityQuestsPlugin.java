package com.github.philipkoivunen.quality_quests;

import com.github.hornta.commando.CarbonArgument;
import com.github.hornta.commando.CarbonCommand;
import com.github.hornta.commando.Commando;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.messenger.MessagesBuilder;
import com.github.hornta.messenger.Translation;
import com.github.hornta.messenger.Translations;
import com.github.hornta.versioned_config.*;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.apis.fileApi.FileApi;
import com.github.philipkoivunen.quality_quests.commands.CreateQuest;
import com.github.philipkoivunen.quality_quests.commands.QquestReload;
import com.github.philipkoivunen.quality_quests.commands.handlers.QuestGoalCompleteParticipationHandler;
import com.github.philipkoivunen.quality_quests.commands.handlers.QuestGoalTypeHandler;
import com.github.philipkoivunen.quality_quests.constants.ConfigConstants;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class QualityQuestsPlugin extends JavaPlugin {
    private static QualityQuestsPlugin instance;
    private Commando commando;
    private Configuration<ConfigConstants> configuration;
    private Translations translations;
    private StorageApi storageApi;

    @Override
    public void onEnable() {
        instance = this;
        this.storageApi = new FileApi(this);
        try {
            setupConfig();
        } catch (ConfigurationException e) {
            getLogger().severe("Failed to setup configuration: " + e.getMessage());
            setEnabled(false);
            return;
        }
        setupMessages();
        setupCommands();
    }

    private void setupConfig() throws ConfigurationException {
        File cfgFile = new File(getDataFolder(), "config.yml");
        ConfigurationBuilder<ConfigConstants> cb = new ConfigurationBuilder<>(cfgFile);
        cb.addMigration(new Migration(1, () -> {
            Patch<ConfigConstants> patch = new Patch<>();
            patch.set(ConfigConstants.LANGUAGE, "language", "english", Type.STRING);
            return patch;
        }));
        this.configuration = cb.create();
    }

    private void setupMessages() {
        MessageManager messageManager = new MessagesBuilder()
                .add(MessageConstants.NO_PERMISSION, "no_permission")
                .add(MessageConstants.DEFAULT_ERROR, "default_error")
                .add(MessageConstants.MISSING_ARGUMENT, "missing_argument")
                .add(MessageConstants.CONFIGURATION_RELOAD_SUCCESS, "configuration_reload_success")
                .add(MessageConstants.CONFIGURATION_RELOAD_FAILURE, "configuration_reload_failure")
                .add(MessageConstants.CREATE_QUEST_SUCCESS, "create_quest_success")
                .add(MessageConstants.CREATE_QUEST_FAILED_PARAM, "create_quest_failed_param")
                .build();

        translations = new Translations(this, messageManager);
        Translation translation = translations.createTranslation(configuration.get(ConfigConstants.LANGUAGE));
        messageManager.setTranslation(translation);
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.commando.handleAutoComplete(sender, command, args);
    }

    private void setupCommands() {
        this.commando = new Commando();
        this.commando.setNoPermissionHandler((CommandSender sender, CarbonCommand command) -> MessageManager.sendMessage(sender, MessageConstants.NO_PERMISSION));

        this.commando
                .addCommand("qquests reload")
                .withHandler(new QquestReload())
                .requiresPermission("qquests.reload");

        this.commando
                .addCommand("qquests createQuest")
                .withHandler(new CreateQuest(this.storageApi))
                .withArgument(
                        new CarbonArgument.Builder("goal_type")
                        .setHandler(new QuestGoalTypeHandler())
                        .create()
                        )

                .withArgument(
                        new CarbonArgument.Builder("complete_participation")
                                .setHandler(new QuestGoalCompleteParticipationHandler())
                                .create()
                )
                .requiresPermission("qquests.create");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return  this.commando.handleCommand(sender, command, args);
    }

    public static QualityQuestsPlugin getInstance() {
        return instance;
    }

    public Configuration<ConfigConstants> getConfiguration() {
        return this.configuration;
    }

    public StorageApi getStorageApi() { return this.storageApi; }

    public Translations getTranslations() {
        return this.translations;
    }

    //public ProtocolManager getProtocolManager() {
    //   return protocolManager;
    //}
}
