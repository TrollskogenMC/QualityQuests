package com.github.philipkoivunen.quality_quests.managers;

import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
import com.google.gson.JsonObject;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        Instant yesterDay = todaysDate.minus(1, ChronoUnit.DAYS);

        if(lastJoinDate.isBefore(yesterDay)) {
            List<OngoingQuest> foundOngoingQuests = new ArrayList<>();
            List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByType(QuestTypeConstants.LOGIN.toString());

            patchAndAddProgress(foundOngoingQuests, foundQuests, user);
        }
    }

    public void onKillEvent(Player player, EntityType entityType) {
        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<OngoingQuest> foundOngoingQuests = new ArrayList<>();
        List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByMobToKill(entityType);

        patchAndAddProgress(foundOngoingQuests, foundQuests, user);
    }


    public void onBreakBlockEvent(Player player, Block block) {
        UserObject user = TrollskogenCorePlugin.getUser(player);
        List<OngoingQuest> foundOngoingQuests = new ArrayList<>();
        List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByBlockToBreak(block.getType());

        patchAndAddProgress(foundOngoingQuests, foundQuests, user);
    }

    private void patchAndAddProgress(List<OngoingQuest> foundOngoingQuests, List<Quest> foundQuests, UserObject user) {
        if(foundOngoingQuests.size() > 0) {
            for(Quest q : foundQuests) {
                List<OngoingQuest> activeQuests = qualityQuestsPlugin.getOngoingQuests().getPlayersActiveOngoingQuestsByQuestId(user.getId(), q.questId);
                if(activeQuests.size() > 0) {
                    for(OngoingQuest o : activeQuests) {
                        o.participation = o.participation + 1;

                        if(o.participation == q.minParticipation) o.isComplete = true;

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
        return json;
    }

}
