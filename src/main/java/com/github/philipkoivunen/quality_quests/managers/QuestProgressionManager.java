package com.github.philipkoivunen.quality_quests.managers;

import com.comphenix.protocol.PacketType;
import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import com.github.philipkoivunen.quality_quests.objects.*;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestProgressionManager {
    QualityQuestsPlugin qualityQuestsPlugin;
    OngoingQuests ongoingQuests;
    Quests quests;

    public QuestProgressionManager(QualityQuestsPlugin qualityQuestsPlugin) {
        this.qualityQuestsPlugin = qualityQuestsPlugin;
        this.ongoingQuests = qualityQuestsPlugin.getOngoingQuests();
        this.quests = qualityQuestsPlugin.getQuests();
    }

    public void onLoginEvent(Player player) {
        if(player.hasPermission("qquests.player")) {
            UserObject user = TrollskogenCorePlugin.getUser(player);
            Instant lastJoinDate = user.getLastJoinDate();
            Instant todaysDate = Instant.now();
            Instant todayZeroZero = todaysDate.truncatedTo(ChronoUnit.DAYS);

            if (lastJoinDate.isBefore(todayZeroZero)) {
                List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByType(QuestTypeConstants.LOGIN.toString());
                this.addNewQuests(user);

                if(foundQuests.size() == 0) patchAndAddProgress(foundQuests, user);
            }
        }
    }

    public void addNewQuests(UserObject user) {
        List<Playlist> foundPlayLists = qualityQuestsPlugin.getPlayLists().getPlayListsCanBeActivated();
        Boolean hasGenerated = false;

        for (Playlist playlist : foundPlayLists) {
            Boolean hasAnyQuestFromPlayList = false;
            int playlistNumToGenerate = playlist.amountToGenerate;
            int amountOfFoundQuests = 0;

            for (UUID questId : playlist.questIds) {
                List<OngoingQuest> activeOngoingQuests = this.ongoingQuests.getPlayersActiveExpiringOngoingQuestsByQuestId(user.getId(), questId);
                if (activeOngoingQuests.size() > 0) {
                    hasAnyQuestFromPlayList = true;
                    amountOfFoundQuests = amountOfFoundQuests + activeOngoingQuests.size();
                }
            }

            int numToGenerate = playlistNumToGenerate - amountOfFoundQuests;
            if (!hasAnyQuestFromPlayList && numToGenerate > 0) {
                generateQuest(numToGenerate, user, playlist);
                hasGenerated = true;
            }
        }

        if (hasGenerated) {
            MessageManager.sendMessage(user.getPlayer(), MessageConstants.NEW_QUESTS);
        }
    }

    public void generateQuest(int numToGenerate, UserObject user, Playlist playlist) {
        for(int i = 0; i < numToGenerate; i++) {
            Instant todaysDate = Instant.now();
            Instant expirationDate = playlist.daysToComplete != null ? todaysDate.plus(playlist.daysToComplete, ChronoUnit.DAYS) : null;
            Quest quest = this.quests.getQuestByUUID(playlist.getRandomQuestId());
            this.qualityQuestsPlugin.getOngoingQuestManager().postOngoingQuest(user, new OngoingQuest(0, user.getId(), quest.questId, 0, true, false, quest.questName, todaysDate, playlist.daysToComplete != null ? expirationDate : null));
        }
    }

    public void onKillEvent(Player player, EntityType entityType) {
        if(player.hasPermission("qquests.player")) {
            UserObject user = TrollskogenCorePlugin.getUser(player);
            List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByMobToKill(entityType);

            if(foundQuests.size() > 0)
            patchAndAddProgress(foundQuests, user);
        }
    }

    public void onBreakBlockEvent(Player player, Block block) {
        if(player.hasPermission("qquests.player")) {
            UserObject user = TrollskogenCorePlugin.getUser(player);
            List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByBlockToBreak(block.getType());
            if (foundQuests.size() > 0)
            patchAndAddProgress(foundQuests, user);
        }
    }

    private void patchAndAddProgress(List<Quest> foundQuests, UserObject user) {
        List<OngoingQuest> foundOngoingQuests = new ArrayList<>();
        if(foundQuests.size() > 0) {
            for(Quest q : foundQuests) {
                List<OngoingQuest> activeQuests = this.ongoingQuests.getPlayersActiveOngoingQuestsByQuestId(user.getId(), q.questId);
                if(activeQuests.size() > 0) {
                    for(OngoingQuest o : activeQuests) {
                        o.participation = o.participation + 1;

                        if(o.participation >= q.minParticipation && o.isActive && !o.isComplete) {
                            o.isComplete = true;
                            o.isActive = false;

                            if(q.commands != null && q.commands.size() > 0) {
                                for(String command : q.commands) {
                                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                                }
                            }

                            MessageManager.setValue("quest_name", q.questName);
                            MessageManager.sendMessage(user.getPlayer(), MessageConstants.QUEST_COMPLETED);
                        } else {
                            Instant lastInteractedDate = o.lastInteractedWidth;
                            Instant todaysDate = Instant.now();
                            Instant threeMinutesAfterInteract = lastInteractedDate == null ? null : lastInteractedDate.plus(3, ChronoUnit.MINUTES);
                            if(threeMinutesAfterInteract == null || todaysDate.isAfter(threeMinutesAfterInteract)) {
                                MessageManager.setValue("progress_current", o.participation);
                                MessageManager.setValue("progress_max", q.minParticipation);
                                MessageManager.setValue("quest_name", q.questName);
                                MessageManager.sendMessage(user.getPlayer(), MessageConstants.QUEST_PROGRESSED);

                                o.lastInteractedWidth = todaysDate;
                            }
                        }

                        foundOngoingQuests.add(o);

                        JsonObject json = generateOngoingQuestJson(o);

                        qualityQuestsPlugin.getServer().getScheduler().runTask(qualityQuestsPlugin, () -> qualityQuestsPlugin.getOngoingQuestManager().patchOngoingQuest(o, json));
                        this.ongoingQuests.deleteOngoingQuest(o.id);
                        this.ongoingQuests.addOngoingQuest(o);
                    }
                }
            }
        }
    }


    private JsonObject generateOngoingQuestJson(OngoingQuest o) {
        JsonObject json = new JsonObject();
        json.addProperty("id", o.id);
        json.addProperty("quest_id", o.questId.toString());
        json.addProperty("user_id", o.userId);
        json.addProperty("is_active", o.isActive);
        json.addProperty("is_complete", o.isComplete);
        json.addProperty("participation", o.participation);
        json.addProperty("name", o.name);
        json.addProperty("activated_on", Instant.now().toString());
        json.addProperty("expires_on", o.expiresOn == null ? "null" : o.expiresOn.toString());
        return json;
    }

}
