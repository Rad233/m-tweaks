package me.melontini.tweaks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.networks.ServerSideNetworking;
import me.melontini.tweaks.registries.BlockRegistry;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.registries.ResourceConditionRegistry;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import me.melontini.tweaks.util.WorldUtil;
import me.melontini.tweaks.util.data.EggProcessingData;
import me.melontini.tweaks.util.data.PlantData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tweaks implements ModInitializer {

    public static final EntityAttributeModifier LEAF_SLOWNESS = new EntityAttributeModifier(UUID.fromString("f72625eb-d4c4-4e1d-8e5c-1736b9bab349"), "Leaf Slowness", -0.3, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    public static final String MODID = "m-tweaks";
    public static TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING_SCREEN_HANDLER;
    public static Map<Identifier, PlantData> PLANT_DATA = new HashMap<>();
    public static Map<Identifier, EggProcessingData> EGG_DATA = new HashMap<>();
    public static DefaultParticleType KNOCKOFF_TOTEM_PARTICLE = FabricParticleTypes.simple();
    public static Table<Item, ItemGroup, Item> ITEM_GROUP_OVERRIDES = HashBasedTable.create();

    @Override
    public void onInitialize() {
        BlockRegistry.register();
        ItemRegistry.register();
        EntityTypeRegistry.register();
        ServerSideNetworking.register();
        ResourceConditionRegistry.register();

        for (BoatEntity.Type type : BoatEntity.Type.values()) {
            if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
                ITEM_GROUP_OVERRIDES.put(Registry.ITEM.get(new Identifier(MODID, type.getName() + "_boat_with_furnace")), ItemGroup.TRANSPORTATION, Registry.ITEM.get(new Identifier("minecraft", type.getName() + "_chest_boat")));
            if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
                ITEM_GROUP_OVERRIDES.put(Registry.ITEM.get(new Identifier(MODID, type.getName() + "_boat_with_jukebox")), ItemGroup.TRANSPORTATION, Registry.ITEM.get(new Identifier("minecraft", type.getName() + "_chest_boat")));
            if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
                ITEM_GROUP_OVERRIDES.put(Registry.ITEM.get(new Identifier(MODID, type.getName() + "_boat_with_tnt")), ItemGroup.TRANSPORTATION, Registry.ITEM.get(new Identifier("minecraft", type.getName() + "_chest_boat")));
            if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
                ITEM_GROUP_OVERRIDES.put(Registry.ITEM.get(new Identifier(MODID, type.getName() + "_boat_with_hopper")), ItemGroup.TRANSPORTATION, Registry.ITEM.get(new Identifier("minecraft", type.getName() + "_chest_boat")));
        }

        ITEM_GROUP_OVERRIDES.put(ItemRegistry.SPAWNER_MINECART, ItemGroup.TRANSPORTATION, Items.HOPPER_MINECART);
        if (CONFIG.newMinecarts.isAnvilMinecartOn)
            ITEM_GROUP_OVERRIDES.put(ItemRegistry.ANVIL_MINECART, ItemGroup.TRANSPORTATION, Items.HOPPER_MINECART);
        if (CONFIG.newMinecarts.isNoteBlockMinecartOn)
            ITEM_GROUP_OVERRIDES.put(ItemRegistry.NOTE_BLOCK_MINECART, ItemGroup.TRANSPORTATION, Items.HOPPER_MINECART);
        if (CONFIG.newMinecarts.isJukeboxMinecartOn)
            ITEM_GROUP_OVERRIDES.put(ItemRegistry.JUKEBOX_MINECART, ItemGroup.TRANSPORTATION, Items.HOPPER_MINECART);

        if (CONFIG.incubatorSettings.enableIncubator)
            ITEM_GROUP_OVERRIDES.put(ItemRegistry.INCUBATOR, ItemGroup.DECORATIONS, Items.STONECUTTER);
        if (CONFIG.totemSettings.enableInfiniteTotem)
            ITEM_GROUP_OVERRIDES.put(ItemRegistry.INFINITE_TOTEM, ItemGroup.COMBAT, Items.TOTEM_OF_UNDYING);

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
    }
}
