package me.melontini.tweaks;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.networks.ServerSideNetworking;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.registries.ResourceConditionRegistry;
import me.melontini.tweaks.screens.FletchingScreenHandler;
import me.melontini.tweaks.util.MiscUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

public class Tweaks implements ModInitializer {

    public static final EntityAttributeModifier LEAF_SLOWNESS = new EntityAttributeModifier(UUID.fromString("f72625eb-d4c4-4e1d-8e5c-1736b9bab349"), "Leaf Slowness", -0.3, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    public static final String MODID = "m-tweaks";
    public static TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING_SCREEN_HANDLER;

    @Override
    public void onInitialize() {
        ItemRegistry.register();
        EntityTypeRegistry.register();
        ServerSideNetworking.register();
        ResourceConditionRegistry.register();

        if (CONFIG.usefulFletching) {
            FLETCHING_SCREEN_HANDLER = new ScreenHandlerType<>(FletchingScreenHandler::new);
            Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID, "fletching"), FLETCHING_SCREEN_HANDLER);
        }

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (CONFIG.tradingGoatHorn) if (world.getRegistryKey() == World.OVERWORLD)
                MiscUtil.getTraderManager(world);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> server.getWorlds().forEach(world -> {
            var manager = world.getPersistentStateManager();
            if (CONFIG.tradingGoatHorn) if (manager.loadedStates.containsKey("mt_trader_statemanager")) {
                    MiscUtil.getTraderManager(world).setDirty(true);
                }
        }));

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            var manager = world.getPersistentStateManager();
            if (CONFIG.tradingGoatHorn) if (world.getRegistryKey() == World.OVERWORLD)
                if (manager.loadedStates.containsKey("mt_trader_statemanager")) {
                    MiscUtil.getTraderManager(world).tick();
                }
        });
    }
}
