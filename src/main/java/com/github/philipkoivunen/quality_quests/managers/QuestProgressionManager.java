package com.github.philipkoivunen.quality_quests.managers;

import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestProgressionManager {
    QualityQuestsPlugin qualityQuestsPlugin;

    public QuestProgressionManager(QualityQuestsPlugin qualityQuestsPlugin) {
        this.qualityQuestsPlugin = qualityQuestsPlugin;
    }

    public void onLoginEvent(Player player) {
        UserObject user = TrollskogenCorePlugin.getUser(player);
        Instant lastJoinDate = user.getLastJoinDate();
        Instant todaysDate = Instant.now();
        Instant todayZeroZero = todaysDate.truncatedTo(ChronoUnit.DAYS);
        // TODO: Kolla mot playList om det finns utgångna quests och ta bort de isåfall och meddela användaren om det

        if(lastJoinDate.isBefore(todayZeroZero)) {
            List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByType(QuestTypeConstants.LOGIN.toString());

            patchAndAddProgress(foundQuests, user);
        }
    }

    public void onKillEvent(Player player, EntityType entityType) {
        // TODO: Kolla mot playList om det finns utgångna quests och ta bort de isåfall och meddela användaren om det

        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByMobToKill(entityType);

        patchAndAddProgress(foundQuests, user);
    }


    public void onBreakBlockEvent(Player player, Block block) {
        // TODO: Kolla mot playList om det finns utgångna quests och ta bort de isåfall och meddela användaren om det

        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByBlockToBreak(block.getType());

        patchAndAddProgress(foundQuests, user);
    }

    private void patchAndAddProgress(List<Quest> foundQuests, UserObject user) {
        List<OngoingQuest> foundOngoingQuests = new ArrayList<>();
        if(foundQuests.size() > 0) {
            for(Quest q : foundQuests) {
                List<OngoingQuest> activeQuests = qualityQuestsPlugin.getOngoingQuests().getPlayersActiveOngoingQuestsByQuestId(user.getId(), q.questId);
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
                        qualityQuestsPlugin.getOngoingQuests().deleteOngoingQuest(o.id);
                        qualityQuestsPlugin.getOngoingQuests().addOngoingQuest(o);
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
        return json;
    }

}
