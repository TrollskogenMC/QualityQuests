package com.github.philipkoivunen.quality_quests.managers;

import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.events.PluginReadyEvent;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.deserializers.OngoingQuestDeserializer;
import com.github.philipkoivunen.quality_quests.deserializers.PatchedOngoingQuestDeserializer;
import com.github.philipkoivunen.quality_quests.deserializers.PostedOngoingQuestDeserializer;
import com.github.philipkoivunen.quality_quests.events.DeleteOngoingQuestsEvent;
import com.github.philipkoivunen.quality_quests.events.LoadOngoingQuestsEvent;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class OngoingQuestManager implements Listener {
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final QualityQuestsPlugin qualityQuestsPlugin;
    private final OngoingQuests ongoingQuests;
    private final Map<Integer, ScheduledFuture> scheduledToExpire;

    public static DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withLocale(Locale.UK).withZone(ZoneId.systemDefault());

    public OngoingQuestManager() {
        qualityQuestsPlugin = QualityQuestsPlugin.getInstance();
        ongoingQuests = qualityQuestsPlugin.getOngoingQuests();
        scheduledToExpire = new HashMap<>();
    }

    public static OngoingQuest parseOngoingQuest(@NotNull JsonObject json) {
        return new OngoingQuest(json.get("id").getAsInt(),
            json.get("user_id").getAsInt(),
            UUID.fromString(json.get("quest_id").getAsString()),
            json.get("participation").getAsInt(),
            json.get("is_active").getAsBoolean(),
            json.get("is_complete").getAsBoolean(),
            json.get("name").getAsString(),
            Instant.parse(json.get("activated_on").getAsString()),
            json.get("expires_on").isJsonNull() || json.get("expires_on").getAsString().equals("null") ? null : Instant.parse(json.get("expires_on").getAsString())
        );
    }

    public void postOngoingQuest(UserObject userObject, OngoingQuest ongoingQuest) {
        JsonObject json = new JsonObject();
        json.addProperty("quest_id", ongoingQuest.questId.toString());
        json.addProperty("user_id", ongoingQuest.userId);
        json.addProperty("is_active", ongoingQuest.isActive);
        json.addProperty("is_complete", ongoingQuest.isComplete);
        json.addProperty("participation", ongoingQuest.participation);
        json.addProperty("name", ongoingQuest.name);
        json.addProperty("expires_on", ongoingQuest.expiresOn == null ? null : formatter.format(ongoingQuest.expiresOn));
        json.addProperty("activated_on", ongoingQuest.activatedOn == null ? null : formatter.format(ongoingQuest.activatedOn));

        scheduledExecutor.submit(() -> {
            TrollskogenCorePlugin.request("POST", "/ongoingquests", json, (Response response) -> {
                Gson gson = new GsonBuilder().
                        registerTypeAdapter(OngoingQuest.class, new PostedOngoingQuestDeserializer())
                        .create();
                OngoingQuest parsedOngoingQuest;

                try {
                    parsedOngoingQuest = gson.fromJson(response.getResponseBody(), OngoingQuest.class);
                } catch (Throwable ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                    return;
                }

                Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
                    ongoingQuests.addOngoingQuest(parsedOngoingQuest);
                    return null;
                });
            });
        });
    }

    public void loadAllOngoingQuests() {
        scheduledExecutor.submit(() -> {
            TrollskogenCorePlugin.request("GET", "/ongoingquests/active", (Response response) -> {
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

                        if(o.expiresOn != null && o.isActive) {
                            this.scheduleHandleExpiredOngoingQuest(o);
                        }
                    }
                    Bukkit.getLogger().info("Loaded " + parsedOngoingQuests.length+ " ongoingquests");

                    Bukkit.getPluginManager().callEvent(new LoadOngoingQuestsEvent(this));
                    Bukkit.getPluginManager().callEvent(new PluginReadyEvent(QualityQuestsPlugin.getInstance()));
                   return null;
                });
            });
        });
    }

    public void patchOngoingQuest(OngoingQuest ongoingQuest, JsonObject json) {
        scheduledExecutor.submit(() -> {
            TrollskogenCorePlugin.request("PATCH", "/ongoingquests/" + ongoingQuest.id, json,(Response response) -> {
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

    public void deleteOngoingQuest(OngoingQuest ongoingQuest) {
        scheduledExecutor.submit(() -> {
           TrollskogenCorePlugin.request("DELETE", "/ongoingquests/" + ongoingQuest.id, (Response response) -> {
              Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
                if(response.getStatusCode() == 200) {
                    ongoingQuests.deleteOngoingQuest(ongoingQuest.id);
                }
                DeleteOngoingQuestsEvent event = new DeleteOngoingQuestsEvent(ongoingQuest);
                Bukkit.getPluginManager().callEvent(event);
                return null;
              });
           });
        });
    }

    public JsonObject generateOngoingQuestJson(OngoingQuest o) {
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

    public void scheduleHandleExpiredOngoingQuest(OngoingQuest ongoingQuest) {
        Instant now = Instant.now();
        Instant expiryDate = ongoingQuest.expiresOn;
        Instant expiriesZeroZero = expiryDate.truncatedTo(ChronoUnit.DAYS);

        long duration = Duration.between(now, expiriesZeroZero).getSeconds() + 1;

        if(duration <= 0 ) {
            Player player = TrollskogenCorePlugin.getUser(ongoingQuest.userId).getPlayer();

            if(ongoingQuest.isComplete) {
                ongoingQuest.isActive = false;
                this.ongoingQuests.addOngoingQuest(ongoingQuest);
                JsonObject json = generateOngoingQuestJson(ongoingQuest);
                this.patchOngoingQuest(ongoingQuest, json);
            } else {
                this.ongoingQuests.deleteOngoingQuest(ongoingQuest.id);
                this.deleteOngoingQuest(ongoingQuest);
            }

            if(player.isOnline()) {
                MessageManager.setValue("quest_name", ongoingQuest.name);
                MessageManager.sendMessage(player, MessageConstants.ENDED_QUEST);

                // there is no need to generate new ongoingquests if the user wont login again
                this.qualityQuestsPlugin.getQuestProgressionManager().addNewQuests(TrollskogenCorePlugin.getUser(ongoingQuest.userId));
            }
        }

        scheduledToExpire.put(ongoingQuest.id, scheduledExecutor.schedule(() -> {
            Instant otherNow = Instant.now();
            Instant otherExpiryDate = ongoingQuest.expiresOn;
            Instant otherExpiriesZeroZero = otherExpiryDate.truncatedTo(ChronoUnit.DAYS);

            if(otherNow.isAfter(otherExpiriesZeroZero)) {
                Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getPlugin(), () -> {
                    if(ongoingQuest.isComplete) {
                        ongoingQuest.isActive = false;
                        this.ongoingQuests.addOngoingQuest(ongoingQuest);
                        JsonObject json = generateOngoingQuestJson(ongoingQuest);
                        this.patchOngoingQuest(ongoingQuest, json);
                    } else {
                        this.ongoingQuests.deleteOngoingQuest(ongoingQuest.id);
                        this.deleteOngoingQuest(ongoingQuest);
                    }
                    Player player = TrollskogenCorePlugin.getUser(ongoingQuest.userId).getPlayer();
                    if(player.isOnline()) {
                        MessageManager.setValue("quest_name", ongoingQuest.name);
                        MessageManager.sendMessage(player, MessageConstants.ENDED_QUEST);
                        // there is no need to generate new ongoingquests if the user wont login again
                        this.qualityQuestsPlugin.getQuestProgressionManager().addNewQuests(TrollskogenCorePlugin.getUser(ongoingQuest.userId));
                    }
                    return null;
                });
            }
        }, duration, TimeUnit.SECONDS));
    }
}
