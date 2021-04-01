package com.github.philipkoivunen.quality_quests.deserializers;

import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class OngoingQuestDeserializer implements JsonDeserializer<OngoingQuest[]> {

    @Override
    public OngoingQuest[] deserialize(JsonElement elem, Type type, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<OngoingQuest> ongoingQuests = new ArrayList<>();

        for(JsonElement j : elem.getAsJsonObject().getAsJsonArray("ongoingQuests")) {
            JsonObject json = j.getAsJsonObject();
            OngoingQuest ongoingQuest = OngoingQuestManager.parseOngoingQuest(json);

            if(ongoingQuest != null) {
                ongoingQuests.add(ongoingQuest);
            }
        }

        return ongoingQuests.toArray(new OngoingQuest[0]);
    }
}
