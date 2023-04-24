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
import java.util.*;

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
                        List<String> fields = Arrays.stream(configOption.split("\\.")).toList();

                        if (fields.size() > 1) {//🤯🤯🤯
                            Object obj = TweaksConfig.class.getField(fields.get(0)).get(Tweaks.CONFIG);
                            for (int i = 1; i < (fields.size() - 1); i++) {
                                obj = obj.getClass().getField(fields.get(i)).get(obj);
                            }
                            load = obj.getClass().getField(fields.get(1)).getBoolean(obj);
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
                        JsonObject json = JsonHelper.deserialize(new InputStreamReader(entry.getValue().getInputStream()));
                        ItemBehaviorData data = new ItemBehaviorData();
                        data.item_id = JsonHelper.getString(json, "item_id");

                        if (Registry.ITEM.get(Identifier.tryParse(data.item_id)) == Items.AIR) {
                            throw new InvalidIdentifierException(String.format("(m-tweaks) invalid identifier provided! %s", data.item_id));
                        }

                        var item_arr = JsonHelper.getArray(json, "item_commands", null);
                        if (item_arr != null) {
                            List<String> item_commands = new ArrayList<>(item_arr.size());
                            for (JsonElement element : item_arr) {
                                item_commands.add(element.getAsString());
                            }
                            data.item_commands = item_commands.toArray(String[]::new);
                        }


                        var user_arr = JsonHelper.getArray(json, "user_commands", null);
                        if (user_arr != null) {
                            List<String> user_commands = new ArrayList<>(user_arr.size());
                            for (JsonElement element : user_arr) {
                                user_commands.add(element.getAsString());
                            }
                            data.user_commands = user_commands.toArray(String[]::new);
                        }

                        var server_arr = JsonHelper.getArray(json, "server_commands", null);
                        if (server_arr != null) {
                            List<String> server_commands = new ArrayList<>(server_arr.size());
                            for (JsonElement element : server_arr) {
                                server_commands.add(element.getAsString());
                            }
                            data.server_commands = server_commands.toArray(String[]::new);
                        }

                        data.complement = JsonHelper.getBoolean(json, "complement",  false);

                        data.spawn_colored_particles = JsonHelper.getBoolean(json, "spawn_colored_particles", false);

                        JsonObject colors = JsonHelper.getObject(json, "particle_colors", new JsonObject());
                        data.particle_colors = new ItemBehaviorData.ParticleColors();
                        data.particle_colors.red = JsonHelper.getInt(colors, "red", 0);
                        data.particle_colors.green = JsonHelper.getInt(colors, "green", 0);
                        data.particle_colors.blue = JsonHelper.getInt(colors, "blue", 0);

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
