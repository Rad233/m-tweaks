package me.melontini.tweaks.registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.melontini.crackerutil.CrackerLog;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.data.EggProcessingData;
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
            List<Boolean> booleans = new ArrayList<>();

            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    try {
                        String elementString = element.getAsString();
                        List<String> classes = Arrays.stream(elementString.split("\\.")).toList();
                        if (classes.size() > 1) {
                            booleans.add(Tweaks.CONFIG.getClass().getField(classes.get(0)).get(Tweaks.CONFIG).getClass().getField(classes.get(1)).getBoolean(Tweaks.CONFIG.getClass().getField(classes.get(0)).get(Tweaks.CONFIG)));
                        } else
                            booleans.add(Tweaks.CONFIG.getClass().getField(elementString).getBoolean(Tweaks.CONFIG));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return booleans.stream().allMatch(aBoolean -> aBoolean);
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
                        LogUtil.info(jsonElement);
                        PlantData data = GSON.fromJson(jsonElement, PlantData.class);

                        if (Registry.BLOCK.get(Identifier.tryParse(data.identifier)) == Blocks.AIR) {
                            throw new InvalidIdentifierException(String.format("[m-tweaks] invalid identifier provided! %s", data.identifier));
                        }

                        Tweaks.PLANT_DATA.putIfAbsent(Registry.BLOCK.get(Identifier.tryParse(data.identifier)), data);
                    } catch (IOException e) {
                        CrackerLog.error("Error while parsing JSON for mt_crop_temperatures", e);
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
                        LogUtil.info(jsonElement);
                        EggProcessingData data = GSON.fromJson(jsonElement, EggProcessingData.class);

                        if (Registry.ENTITY_TYPE.get(Identifier.tryParse(data.entity)) == EntityType.PIG && !Objects.equals(data.entity, "minecraft:pig")) {
                            throw new InvalidIdentifierException(String.format("[m-tweaks] invalid entity identifier provided! %s", data.entity));
                        }

                        if (Registry.ITEM.get(Identifier.tryParse(data.identifier)) == Items.AIR) {
                            throw new InvalidIdentifierException(String.format("[m-tweaks] invalid item identifier provided! %s", data.identifier));
                        }

                        Tweaks.EGG_DATA.putIfAbsent(Registry.ITEM.get(Identifier.tryParse(data.identifier)), data);
                    } catch (IOException e) {
                        CrackerLog.error("Error while parsing JSON for mt_egg_processing", e);
                    }
                }
            }
        });
        LogUtil.info("ResourceConditionRegistry init complete");
    }
}
