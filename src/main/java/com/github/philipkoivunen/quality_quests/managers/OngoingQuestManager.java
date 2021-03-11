package com.github.philipkoivunen.quality_quests.managers;

import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.events.PluginReadyEvent;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_core.users.events.LoadUsersEvent;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.deserializers.OngoingQuestDeserializer;
import com.github.philipkoivunen.quality_quests.deserializers.PatchedOngoingQuestDeserializer;
import com.github.philipkoivunen.quality_quests.events.DeleteOngoingQuestsEvent;
import com.github.philipkoivunen.quality_quests.events.LoadOngoingQuestsEvent;
import com.github.philipkoivunen.quality_quests.events.RequestDeleteOngoingQuestEvent;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

public class OngoingQuestManager implements Listener {
    private final HashMap<Integer, List<OngoingQuest>> userToOngoingQuests;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final QualityQuestsPlugin qualityQuestsPlugin;
    private final OngoingQuests ongoingQuests;
    public OngoingQuestManager() {
        userToOngoingQuests = new HashMap<>();
        qualityQuestsPlugin = QualityQuestsPlugin.getInstance();
        ongoingQuests = qualityQuestsPlugin.getOngoingQuests();
    }

    public static OngoingQuest parseOngoingQuest(JsonObject json) {
        return new OngoingQuest(json.get("id").getAsInt(),
            json.get("userId").getAsInt(),
            json.get("questId").getAsInt(),
            json.get("participation").getAsInt(),
            json.get("isActive").getAsBoolean()
        );
    }

    private void postOngoingQuest(UserObject userObject, OngoingQuest ongoingQuest) {
        JsonObject json = new JsonObject();
        json.addProperty("id", ongoingQuest.id);
        json.addProperty("quest_id", ongoingQuest.questId);
        json.addProperty("user_id", ongoingQuest.userId);
        json.addProperty("is_active", ongoingQuest.isActive);

        scheduledExecutor.submit(() -> {

        });
    }

    private void loadAllOngoingQuests() {
        scheduledExecutor.submit(() -> {
            TrollskogenCorePlugin.request("GET", "/ongoingquests", (Response response) -> {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(OngoingQuest[].class, new OngoingQuestDeserializer())
                        .create();
                OngoingQuest[] parsedOngoingQuests;

                try {
                    parsedOngoingQuests = gson.fromJson(response.getResponseBody(), OngoingQuest[].class);
                } catch (Throwable ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    return;
                }

                Bukkit.getScheduler().callSyncMethod(QualityQuestsPlugin.getInstance(), () -> {
                    ongoingQuests.clear();
                   for(OngoingQuest o: parsedOngoingQuests) {
                       ongoingQuests.addOngoingQuest(o);
                   }
                   Bukkit.getLogger().info("Loaded " + parsedOngoingQuests + " ongoingquests");

                    Bukkit.getPluginManager().callEvent(new LoadOngoingQuestsEvent(this));
                    Bukkit.getPluginManager().callEvent(new PluginReadyEvent(QualityQuestsPlugin.getInstance()));
                   return null;
                });
            });
        });
    }

    private void patchOngoingQuest(OngoingQuest ongoingQuest) {
        scheduledExecutor.submit(() -> {
            TrollskogenCorePlugin.request("PATCH", "/ongoingquests/" + ongoingQuest.id, (Response response) -> {
                Gson gson = new GsonBuilder().
                        registerTypeAdapter(OngoingQuest.class, new PatchedOngoingQuestDeserializer())
                        .create();
                OngoingQuest parsedOngoingQuest;

                try {
                    parsedOngoingQuest = gson.fromJson(response.getResponseBody(), OngoingQuest.class);
                } catch (Throwable ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    return;

                }

                Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
                    return null;
                });
            });
        });
    }

    private void deleteOngoingQuest(OngoingQuest ongoingQuest) {
        scheduledExecutor.submit(() -> {
           TrollskogenCorePlugin.request("DELETE", "/ongoingquests/" + ongoingQuest.id, (Response response) -> {
              Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
                if(response.getStatusCode() == 200) {
                    ongoingQuests.deleteOngoingQuest(ongoingQuest);
                    userToOngoingQuests.get(ongoingQuest.userId).remove(ongoingQuest);
                    if(userToOngoingQuests.get(ongoingQuest.userId).isEmpty()) {
                        userToOngoingQuests.remove(ongoingQuest.id);
                    }
                }
                DeleteOngoingQuestsEvent event = new DeleteOngoingQuestsEvent(ongoingQuest);
                Bukkit.getPluginManager().callEvent(event);
                return null;
              });
           });
        });
    }

    @EventHandler
    void onLoadUsers(LoadUsersEvent event) {
        loadAllOngoingQuests();
    }

    @EventHandler
    void onRequestDeleteOngoingQuest(RequestDeleteOngoingQuestEvent event) {
        deleteOngoingQuest(event.getOngoingQuest());
    }

    //@EventHandler
    //void onRequestAddOngoingQuest(RequestAddOngoingQuestEvent event) {

    //}

   // @EventHandler
   // void onRequestPatchOngoingQuest(RequestPatchOngoingQuestEvent event) {
   //     patchOngoingQuest(event.getOngoingQuest());
   // }
}
