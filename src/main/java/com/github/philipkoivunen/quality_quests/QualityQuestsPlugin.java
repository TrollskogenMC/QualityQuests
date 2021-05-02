package com.github.philipkoivunen.quality_quests;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.plugin.Plugin;
import se.hornta.commando.CarbonArgument;
import se.hornta.commando.CarbonArgumentType;
import se.hornta.commando.CarbonCommand;
import se.hornta.commando.Commando;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.events.PluginReadyEvent;
import se.hornta.versioned_config.*;
import com.github.philipkoivunen.quality_quests.apis.StorageApi;
import com.github.philipkoivunen.quality_quests.apis.fileApi.FileApi;
import com.github.philipkoivunen.quality_quests.commands.*;
import com.github.philipkoivunen.quality_quests.commands.handlers.*;
import com.github.philipkoivunen.quality_quests.constants.ConfigConstants;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.events.Events;
import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import com.github.philipkoivunen.quality_quests.managers.QuestProgressionManager;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuests;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import com.github.philipkoivunen.quality_quests.objects.Quests;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import se.hornta.messenger.*;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class QualityQuestsPlugin extends JavaPlugin {
    private static QualityQuestsPlugin instance;
    private Commando commando;
    private Configuration<ConfigConstants> configuration;
    private Translations translations;
    private StorageApi storageApi;
    private Quests quests;
    private OngoingQuests ongoingQuests;
    private OngoingQuestManager ongoingQuestManager;
    private Playlists playLists;
    private Events events;
    private QuestProgressionManager questProgressionManager;
    private CoreProtectAPI coreProtect;

    @Override
    public void onEnable() {
        instance = this;
        this.storageApi = new FileApi(this);
        this.quests = new Quests();
        this.ongoingQuests = new OngoingQuests();
        this.playLists = new Playlists();
        this.events = new Events();

        TrollskogenCorePlugin.getServerReady().waitFor(this);
        try {
            this.setupConfig();
        } catch (ConfigurationException e) {
            getLogger().severe("Failed to setup configuration: " + e.getMessage());
            setEnabled(false);
            return;
        }
        try {
            this.setupMessages();
        } catch (MessengerException e) {
            getLogger().log(Level.SEVERE, "Failed to setup messages", e.getMessage());
            return;
        }
        setupCommands();


        this.storageApi.fetchAllQuests();
        this.storageApi.fetchAllPlaylists();
        getServer().getPluginManager().registerEvents(this.events, this);
        getServer().getPluginManager().registerEvents(this.ongoingQuestManager, this);

        Bukkit.getPluginManager().callEvent(new PluginReadyEvent(this));
        getServer().getScheduler().runTask(this, () -> this.ongoingQuestManager.loadAllOngoingQuests());
        this.coreProtect = initiateCoreprotect();
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
        this.ongoingQuestManager = new OngoingQuestManager();
        this.questProgressionManager = new QuestProgressionManager(this);
    }

    private void setupMessages() throws MessengerException {
        MessagesBuilder m = new MessagesBuilder();
        m.add(MessageConstants.NO_PERMISSION, "no_permission");
        m.add(MessageConstants.DEFAULT_ERROR, "default_error");
        m.add(MessageConstants.MISSING_ARGUMENT, "missing_argument");
        m.add(MessageConstants.CONFIGURATION_RELOAD_SUCCESS, "configuration_reload_success");
        m.add(MessageConstants.CONFIGURATION_RELOAD_FAILURE, "configuration_reload_failure");
        m.add(MessageConstants.CREATE_QUEST_SUCCESS, "create_quest_success");
        m.add(MessageConstants.LIST_QUESTS_TITLE, "list_quests_title");
        m.add(MessageConstants.LIST_QUEST_KILL, "list_quest_kill");
        m.add(MessageConstants.LIST_QUEST_BREAK, "list_quest_break");
        m.add(MessageConstants.LIST_QUEST_CUSTOM, "list_quest_custom");
        m.add(MessageConstants.CREATE_QUEST_FAILED_PARAM, "create_quest_failed_param");
        m.add(MessageConstants.UPDATE_QUEST_ERROR, "update_quest_error");
        m.add(MessageConstants.UPDATE_QUEST_SUCCESS, "update_quest_success");
        m.add(MessageConstants.START_QUEST_SUCCESS, "start_quest_success");
        m.add(MessageConstants.START_QUEST_FAILURE, "start_quest_failure");
        m.add(MessageConstants.QUEST_COMPLETED, "quest_completed");
        m.add(MessageConstants.QUEST_PROGRESSED, "quest_progressed");
        m.add(MessageConstants.CREATE_PLAYLIST_SUCCESS, "create_playlist_success");
        m.add(MessageConstants.UPDATE_PLAYLIST_SUCCESS, "update_playlist_success");
        m.add(MessageConstants.ENDED_QUEST, "ended_quest");
        m.add(MessageConstants.NEW_QUESTS, "new_quests");
        m.add(MessageConstants.LIST_QUEST_EMPTY, "list_quest_empty");

        MessageManager messageManager = m.build();
        this.translations = new Translations(this, messageManager);
        String language = this.configuration.get(ConfigConstants.LANGUAGE);
        Translation translation = this.translations.createTranslation(language);
        MessageManager.getInstance().setTranslation(translation);
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
                .requiresPermission("qquests.admin");

        this.commando
                .addCommand("quests list")
                .withHandler(new QquestsList())
                .requiresPermission("qquests.player");

        this.commando
                .addCommand("qquests quest setMob")
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
                .requiresPermission("qquests.admin");

        this.commando
                .addCommand("qquests quest setBlock")
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
                .requiresPermission("qquests.admin");

        this.commando
            .addCommand("qquests quest create")
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
                            .setHandler(new IntegerHandler())
                            .create()
            )
            .requiresPermission("qquests.admin");

        this.commando
            .addCommand("qquests quest activate")
            .withHandler(new QquestsActivate(this.ongoingQuestManager, this.getQuests(), this.getOngoingQuests()))
            .withArgument(
                    new CarbonArgument.Builder("questId")
                            .setHandler(new QuestIdHandler())
                            .create()
            )
            .withArgument(
                    new CarbonArgument.Builder("player")
                            .setType(CarbonArgumentType.ONLINE_PLAYER)
                            .setDefaultValue(Player.class, (CommandSender sender, String[] prevArgs) -> sender.getName())
                            .requiresPermission("qquests.admin")
                            .create()
            )
            .requiresPermission("qquests.admin");

        this.commando
                .addCommand("qquests quest deactivate")
                .withHandler(new QquestsDeActivate(this.ongoingQuestManager, this.getQuests(), this.getOngoingQuests()))
                .withArgument(
                        new CarbonArgument.Builder("questId")
                                .setHandler(new QuestIdHandler())
                                .create()
                )
                .requiresPermission("qquests.admin");

        this.commando
                .addCommand("qquests playlist create")
                .withHandler(new QquestCreatePlaylist(this.getStorageApi(), this.getPlayLists()))
                .withArgument(
                        new CarbonArgument.Builder("name")
                                .setHandler(new QquestPlaylistName())
                                .create()
                )
                .requiresPermission("qquests.admin");

        this.commando
                .addCommand("qquests playlist add quest")
                .withHandler(new QquestPlaylistAddQuest(this.getStorageApi(), this.getPlayLists(), this.getQuests()))
                .withArgument(
                        new CarbonArgument.Builder("playListId")
                                .setHandler(new PlayListHandler(this.getPlayLists()))
                                .create()
                )
                .withArgument(
                        new CarbonArgument.Builder("questId")
                                .setHandler(new QuestHandler())
                                .create()
                )
                .requiresPermission("qquests.admin");

        this.commando
                .addCommand("qquests playlist activate random")
                .withHandler(new QquestPlaylistActivateRandom(this.getStorageApi(), this.getPlayLists(), this.getQuests(), this.getOngoingQuests(), getOngoingQuestManager()))
                .withArgument(
                        new CarbonArgument.Builder("playListId")
                                .setHandler(new PlaylistId())
                                .create()
                )
                .withArgument(
                        new CarbonArgument.Builder("player")
                                .setType(CarbonArgumentType.ONLINE_PLAYER)
                                .setDefaultValue(Player.class, (CommandSender sender, String[] prevArgs) -> sender.getName())
                                .requiresPermission("qquests.admin")
                                .create()
                )
                .requiresPermission("qquests.admin");

        this.commando
                .addCommand("qquests playlist set daysToComplete")
                .withHandler(new PlaylistDaysSet(this.getStorageApi(), this.getPlayLists()))
                .withArgument(
                        new CarbonArgument.Builder("playlistName")
                                .setHandler(new PlayListHandler(this.getPlayLists()))
                                .create()
                )
                .withArgument(
                        new CarbonArgument.Builder("daysToComplete")
                                .setHandler(new IntegerHandler())
                                .create()
                )
                .requiresPermission("qquests.admin");

        this.commando
                .addCommand("qquests playlist set numToGenerate")
                .withHandler(new PlaylistNumToGenerateSet(this.getStorageApi(), this.getPlayLists()))
                .withArgument(
                    new CarbonArgument.Builder("playlistName")
                        .setHandler(new PlayListHandler(this.getPlayLists()))
                        .create()
                )
                .withArgument(
                    new CarbonArgument.Builder("numToGenerate")
                        .setHandler(new IntegerHandler())
                        .create()
                )
                .requiresPermission("qquests.admin");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.commando.handleCommand(sender, command, args);
    }

    private CoreProtectAPI initiateCoreprotect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 6) {
            return null;
        }

        return CoreProtect;
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
    public QuestProgressionManager getQuestProgressionManager() { return this.questProgressionManager;}
    public OngoingQuestManager getOngoingQuestManager() { return this.ongoingQuestManager;}
    public Playlists getPlayLists() { return this.playLists;}
    public CoreProtectAPI getCoreProtect() {
        return this.coreProtect;
    }


}
