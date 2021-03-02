package com.github.philipkoivunen.quality_quests.deserializers;

import com.github.philipkoivunen.quality_quests.managers.OngoingQuestManager;
import com.github.philipkoivunen.quality_quests.objects.OngoingQuest;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PatchedOngoingQuestDeserializer implements JsonDeserializer<OngoingQuest> {

    @Override
    public OngoingQuest deserialize(JsonElement elem, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = elem.getAsJsonObject().get("patched").getAsJsonObject();
        return OngoingQuestManager.parseOngoingQuest(json);
    }
}
