package me.melontini.tweaks;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.networks.ServerSideNetworking;
import me.melontini.tweaks.registries.BlockRegistry;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.registries.ResourceConditionRegistry;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import me.melontini.tweaks.util.MiscUtil;
import me.melontini.tweaks.util.WorldUtil;
import me.melontini.tweaks.util.data.EggProcessingData;
import me.melontini.tweaks.util.data.PlantData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.melontini.tweaks.util.MiscUtil.hackAdvancements;

public class Tweaks implements ModInitializer {

    public static final String MODID = "m-tweaks";
    public static EntityAttributeModifier LEAF_SLOWNESS;
    public static TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING_SCREEN_HANDLER;
    public static Map<Block, PlantData> PLANT_DATA = new HashMap<>();
    public static Map<Item, EggProcessingData> EGG_DATA = new HashMap<>();
    public static DefaultParticleType KNOCKOFF_TOTEM_PARTICLE;

    @Override
    public void onInitialize() {
        BlockRegistry.register();
        ItemRegistry.register();
        EntityTypeRegistry.register();
        ServerSideNetworking.register();
        ResourceConditionRegistry.register();

        LEAF_SLOWNESS = new EntityAttributeModifier(UUID.fromString("f72625eb-d4c4-4e1d-8e5c-1736b9bab349"), "Leaf Slowness", -0.3, EntityAttributeModifier.Operation.MULTIPLY_BASE);
        KNOCKOFF_TOTEM_PARTICLE = FabricParticleTypes.simple();

        if (CONFIG.usefulFletching) {
            FLETCHING_SCREEN_HANDLER = new ScreenHandlerType<>(FletchingScreenHandler::new);
            Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID, "fletching"), FLETCHING_SCREEN_HANDLER);
        }

        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
            Registry.register(Registry.PARTICLE_TYPE, new Identifier(MODID, "knockoff_totem_particles"), KNOCKOFF_TOTEM_PARTICLE);

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (CONFIG.tradingGoatHorn) if (world.getRegistryKey() == World.OVERWORLD)
                WorldUtil.getTraderManager(world);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            Tweaks.PLANT_DATA.clear();
            Tweaks.EGG_DATA.clear();
            if (CONFIG.tradingGoatHorn) {
                ServerWorld world = server.getWorld(World.OVERWORLD);
                if (world != null) {
                    var manager = world.getPersistentStateManager();
                    if (manager.loadedStates.containsKey("mt_trader_statemanager"))
                        WorldUtil.getTraderManager(world).markDirty();
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (CONFIG.tradingGoatHorn) if (world.getRegistryKey() == World.OVERWORLD) {
                var manager = world.getPersistentStateManager();
                if (manager.loadedStates.containsKey("mt_trader_statemanager"))
                    WorldUtil.getTraderManager(world).tick();
            }
        });

        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.BLASTING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/blasting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.SMOKING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/smoking/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.SMELTING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/smelting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.CAMPFIRE_COOKING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/campfire_cooking/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.STONECUTTING, (map, recipe) -> map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/stonecutting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().get(0))));
        MiscUtil.TYPE_CONSUMER_MAP.put(RecipeType.CRAFTING, (map, recipe) -> {
            if (!(recipe instanceof SpecialCraftingRecipe)) {
                if (!recipe.getIngredients().isEmpty()) {
                    map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/crafting/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().toArray(Ingredient[]::new)));
                }
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (Tweaks.CONFIG.autoGenRecipes.autogenRecipeAdvancements) hackAdvancements(server);
        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, b) -> {
            if (b) if (Tweaks.CONFIG.autoGenRecipes.autogenRecipeAdvancements) hackAdvancements(server);
        });
    }
}
