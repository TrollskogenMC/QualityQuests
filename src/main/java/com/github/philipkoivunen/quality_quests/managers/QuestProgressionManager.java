package com.github.philipkoivunen.quality_quests.managers;

import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.QuestTypeConstants;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.Quest;
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
        System.out.println(lastJoinDate.toString() + ' ' + todaysDate.toString());
        Instant yesterDay = todaysDate.minus(1, ChronoUnit.DAYS);

        if(lastJoinDate.isBefore(yesterDay)) {
            List<OngoingQuest> foundOngoingQuests = new ArrayList<>();
            List<Quest> foundQuests = qualityQuestsPlugin.getQuests().getQuestsByType(QuestTypeConstants.LOGIN.toString());

            for(Quest q : foundQuests) {
                List<OngoingQuest> actIveQuests = qualityQuestsPlugin.getOngoingQuests().getPlayersActiveOngoingQuestsByQuestId(user.getId(), q.questId);
                if(actIveQuests.size() > 0) {
                    for(OngoingQuest o : actIveQuests) {
                        o.participation = o.participation + 1;

                        if(o.participation == q.completeParticipation) o.isComplete = true;

                        foundOngoingQuests.add(o);
                        qualityQuestsPlugin.getServer().getScheduler().runTask(qualityQuestsPlugin, () -> qualityQuestsPlugin.getOngoingQuestManager().patchOngoingQuest(o));
                    }
                }
            }
        }
    }
}
