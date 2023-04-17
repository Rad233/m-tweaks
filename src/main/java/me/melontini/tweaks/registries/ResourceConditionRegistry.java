package me.melontini.tweaks.registries;

import com.google.gson.*;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.TweaksLog;
import me.melontini.tweaks.util.data.EggProcessingData;
import me.melontini.tweaks.util.data.ItemBehaviorData;
import me.melontini.tweaks.util.data.PlantData;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.melontini.tweaks.Tweaks.MODID;

public class ResourceConditionRegistry {

    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void register() {
        ResourceConditions.register(new Identifier(MODID, "config_option"), object -> {
            JsonArray array = JsonHelper.getArray(object, "values");
            boolean load = true;

            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    try {
                        String configOption = element.getAsString();
                        List<String> classes = Arrays.stream(configOption.split("\\.")).toList();

                        if (classes.size() > 1) {//🤯🤯🤯
                            Object obj = TweaksConfig.class.getField(classes.get(0)).get(Tweaks.CONFIG);
                            for (int i = 1; i < (classes.size() - 1); i++) {
                                obj = obj.getClass().getField(classes.get(i)).get(obj);
                            }
                            load = obj.getClass().getField(classes.get(1)).getBoolean(obj);
                        } else {
                            load = Tweaks.CONFIG.getClass().getField(configOption).getBoolean(Tweaks.CONFIG);
                        }
                        if (!load) break;
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return load;
        });
        ResourceConditions.register(new Identifier(MODID, "items_registered"), object -> {
            JsonArray array = JsonHelper.getArray(object, "values");

            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    if (Registry.ITEM.get(new Identifier(element.getAsString())) == Items.AIR) return false;
                }
            }

            return true;
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MODID, "crop_temperatures");
            }

            @Override
            public void reload(ResourceManager manager) {
                Tweaks.PLANT_DATA.clear();
                var map = manager.findResources("mt_crop_temperatures", identifier -> identifier.getPath().endsWith(".json"));
                for (Map.Entry<Identifier, Resource> entry : map.entrySet()) {
                    try {
                        var jsonElement = JsonHelper.deserialize(new InputStreamReader(entry.getValue().getInputStream()));
                        //LogUtil.devInfo(jsonElement);
                        PlantData data = GSON.fromJson(jsonElement, PlantData.class);

                        if (Registry.BLOCK.get(Identifier.tryParse(data.identifier)) == Blocks.AIR) {
                            throw new InvalidIdentifierException(String.format("(m-tweaks) invalid identifier provided! %s", data.identifier));
                        }

                        Tweaks.PLANT_DATA.putIfAbsent(Registry.BLOCK.get(Identifier.tryParse(data.identifier)), data);
                    } catch (IOException e) {
                        TweaksLog.error("Error while parsing JSON for mt_crop_temperatures", e);
                    }
                }
            }
        });


        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MODID, "egg_processing");
            }

            @Override
            public void reload(ResourceManager manager) {
                Tweaks.EGG_DATA.clear();
                //well...
                for (Item item : Registry.ITEM) {
                    if (item instanceof SpawnEggItem spawnEggItem) {
                        Tweaks.EGG_DATA.putIfAbsent(spawnEggItem, new EggProcessingData(Registry.ITEM.getId(spawnEggItem).toString(), Registry.ENTITY_TYPE.getId(spawnEggItem.getEntityType(new NbtCompound())).toString(), 8000));
                    }
                }

                var map = manager.findResources("mt_egg_processing", identifier -> identifier.getPath().endsWith(".json"));
                for (Map.Entry<Identifier, Resource> entry : map.entrySet()) {
                    try {
                        var jsonElement = JsonHelper.deserialize(new InputStreamReader(entry.getValue().getInputStream()));
                        //LogUtil.devInfo(jsonElement);
                        EggProcessingData data = GSON.fromJson(jsonElement, EggProcessingData.class);

                        if (Registry.ENTITY_TYPE.get(Identifier.tryParse(data.entity)) == EntityType.PIG && !Objects.equals(data.entity, "minecraft:pig")) {
                            throw new InvalidIdentifierException(String.format("(m-tweaks) invalid entity identifier provided! %s", data.entity));
                        }

                        if (Registry.ITEM.get(Identifier.tryParse(data.identifier)) == Items.AIR) {
                            throw new InvalidIdentifierException(String.format("(m-tweaks) invalid item identifier provided! %s", data.identifier));
                        }

                        Tweaks.EGG_DATA.putIfAbsent(Registry.ITEM.get(Identifier.tryParse(data.identifier)), data);
                    } catch (IOException e) {
                        TweaksLog.error("Error while parsing JSON for mt_egg_processing", e);
                    }
                }
            }
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MODID, "mt_item_throw_behavior");
            }

            @Override
            public void reload(ResourceManager manager) {//datapacks override everything else
                Tweaks.ITEM_BEHAVIOR_DATA.clear();
                var map = manager.findResources("mt_item_throw_behavior", identifier -> identifier.getPath().endsWith(".json"));
                for (Map.Entry<Identifier, Resource> entry : map.entrySet()) {
                    try {
                        var jsonElement = JsonHelper.deserialize(new InputStreamReader(entry.getValue().getInputStream()));
                        //LogUtil.devInfo(jsonElement);
                        JsonObject json = GSON.fromJson(jsonElement, JsonObject.class);
                        ItemBehaviorData data = new ItemBehaviorData();
                        data.item_id = JsonHelper.getString(json, "item_id");
                        data.function_id = JsonHelper.getString(json, "function_id", null);

                        data.effect_id = JsonHelper.getString(json, "effect_id", null);
                        data.effect_time = JsonHelper.getInt(json, "effect_time", 100);
                        data.effect_level = JsonHelper.getInt(json, "effect_level", 0);

                        data.particle_id = JsonHelper.getString(json, "particle_id", null);
                        data.particle_count = JsonHelper.getInt(json, "particle_count", 10);
                        data.particle_delta_x = JsonHelper.getDouble(json, "particle_delta_x", 0.5);
                        data.particle_delta_y = JsonHelper.getDouble(json, "particle_delta_y", 0.5);
                        data.particle_delta_z = JsonHelper.getDouble(json, "particle_delta_z", 0.5);
                        data.particle_speed = JsonHelper.getDouble(json, "particle_speed", 0.5);
                        data.particle_force = JsonHelper.getBoolean(json, "particle_force", true);

                        if (Registry.ITEM.get(Identifier.tryParse(data.item_id)) == Items.AIR) {
                            throw new InvalidIdentifierException(String.format("(m-tweaks) invalid identifier provided! %s", data.item_id));
                        }

                        Tweaks.ITEM_BEHAVIOR_DATA.putIfAbsent(Registry.ITEM.get(Identifier.tryParse(data.item_id)), data);
                    } catch (IOException e) {
                        TweaksLog.error("Error while parsing JSON for mt_item_drop_behavior", e);
                    }
                }
            }
        });

        TweaksLog.info("ResourceConditionRegistry init complete");
    }
}
