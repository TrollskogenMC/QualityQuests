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
import com.github.philipkoivunen.quality_quests.commands.*;
import com.github.philipkoivunen.quality_quests.commands.handlers.*;
import com.github.philipkoivunen.quality_quests.constants.ConfigConstants;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuests;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;

public class QualityQuestsPlugin extends JavaPlugin {
    private static QualityQuestsPlugin instance;
    private Commando commando;
    private Configuration<ConfigConstants> configuration;
    private Translations translations;
    private StorageApi storageApi;
    private Quests quests;
    private OngoingQuests ongoingQuests;

    @Override
    public void onEnable() {
        instance = this;
        this.storageApi = new FileApi(this);
        this.quests = new Quests();
        this.ongoingQuests = new OngoingQuests();

        TrollskogenCorePlugin.getServerReady().waitFor(this);

        try {
            setupConfig();
        } catch (ConfigurationException e) {
            getLogger().severe("Failed to setup configuration: " + e.getMessage());
            setEnabled(false);
            return;
        }
        setupMessages();
        setupCommands();

        this.storageApi.fetchAllQuests();
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
                .add(MessageConstants.LIST_QUESTS_TITLE, "list_quests_title")
                .add(MessageConstants.LIST_QUEST_KILL, "list_quest_kill")
                .add(MessageConstants.LIST_QUEST_BREAK, "list_quest_break")
                .add(MessageConstants.LIST_QUEST_CUSTOM, "list_quest_custom")
                .add(MessageConstants.CREATE_QUEST_FAILED_PARAM, "create_quest_failed_param")
                .add(MessageConstants.LIST_QUEST_EMPTY, "list_quest_empty")
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
                .addCommand("qquests list")
                .withHandler(new QquestsList())
                .requiresPermission("qquests.list");

        this.commando
                .addCommand("qquests setMob")
                .withHandler(new QquestsSetMob(getInstance(), getQuests(), getStorageApi()))
                .withArgument(
                        new CarbonArgument.Builder("questName")
                                .setHandler(new QuestHandler())
                                .create()
                )
                .withArgument(
                        new CarbonArgument.Builder("mob")
                                .setHandler(new QuestMobHandler())
                                .create()
                )
                .requiresPermission("qquests.setMob");

        this.commando
                .addCommand("qquests setBlock")
                .withHandler(new QquestsSetBlock(getInstance(), getQuests(), getStorageApi()))
                .withArgument(
                        new CarbonArgument.Builder("questName")
                                .setHandler(new QuestHandler())
                                .create()
                )
                .withArgument(
                new CarbonArgument.Builder("block")
                        .setHandler(new QuestBlockHandler())
                        .create()
        )
                .requiresPermission("qquests.setBlock");

        this.commando
                .addCommand("qquests createQuest")
                .withHandler(new CreateQuest(this.storageApi, quests))
                .withArgument(
                        new CarbonArgument.Builder("name")
                                .setHandler(new QuestNameHandler())
                                .create()
                )
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
    public Quests getQuests() { return this.quests; }
    public Translations getTranslations() {
        return this.translations;
    }

    public OngoingQuests getOngoingQuests() {
        return this.ongoingQuests;
    }

    //public ProtocolManager getProtocolManager() {
    //   return protocolManager;
    //}
}
