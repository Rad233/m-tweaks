package me.melontini.tweaks.registries;

import com.google.gson.*;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.PlantData;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

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

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MODID, "crop_temperatures");
            }

            @Override
            public void reload(ResourceManager manager) {
                var map = manager.findResources("mt_crop_temperatures", identifier -> identifier.getPath().endsWith(".json"));
                for (Map.Entry<Identifier, Resource> entry : map.entrySet()) {
                    try {
                        JsonElement jsonElement = JsonHelper.deserialize(new InputStreamReader(entry.getValue().getInputStream()));
                        LogUtil.info(jsonElement);
                        PlantData data = GSON.fromJson(jsonElement, PlantData.class);

                        if (Registry.BLOCK.get(Identifier.tryParse(data.identifier)) == Blocks.AIR) {
                            throw new InvalidIdentifierException(String.format("[m-tweaks] invalid identifier provided! %s", data.identifier));
                        }

                        if (!Tweaks.PLANT_DATA.containsKey(Identifier.tryParse(data.identifier)))
                            Tweaks.PLANT_DATA.put(Identifier.tryParse(data.identifier), data);
                    } catch (IOException e) {
                        LogUtil.error(e);
                    }
                }
            }
        });
    }
}
