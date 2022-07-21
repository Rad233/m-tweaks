package me.melontini.tweaks.registries;

import com.google.gson.*;
import me.melontini.tweaks.util.LogUtil;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import static me.melontini.tweaks.Tweaks.MODID;

public class ResourceConditionRegistry {

    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void register() {
        ResourceConditions.register(new Identifier(MODID, "items_registered"), object -> {
            JsonArray array = JsonHelper.getArray(object, "values");

            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    return Registry.ITEM.get(new Identifier(element.getAsString())) != Items.AIR;
                } else {
                    throw new JsonParseException("Invalid item id entry: " + element);
                }
            }

            return true;
        });
        LogUtil.info("ResourceConditionRegistry init complete");
    }
}
