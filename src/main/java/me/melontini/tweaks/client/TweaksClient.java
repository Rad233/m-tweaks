package me.melontini.tweaks.client;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.render.BoatWithBlockRenderer;
import me.melontini.tweaks.client.screens.FletchingScreen;
import me.melontini.tweaks.networks.ClientSideNetworking;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class TweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ClientSideNetworking.register();

        if (Tweaks.CONFIG.usefulFletching) HandledScreens.register(Tweaks.FLETCHING_SCREEN_HANDLER, FletchingScreen::new);

        if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
            EntityRendererRegistry.INSTANCE.register(EntityTypeRegistry.BOAT_WITH_FURNACE, (manager, context) -> new BoatWithBlockRenderer(manager, Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH)));
        if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
            EntityRendererRegistry.INSTANCE.register(EntityTypeRegistry.BOAT_WITH_JUKEBOX, (manager, context) ->  new BoatWithBlockRenderer(manager, Blocks.JUKEBOX.getDefaultState()));
        if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
            EntityRendererRegistry.INSTANCE.register(EntityTypeRegistry.BOAT_WITH_TNT, (manager, context) -> new BoatWithBlockRenderer(manager, Blocks.TNT.getDefaultState()));
        if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
            EntityRendererRegistry.INSTANCE.register(EntityTypeRegistry.BOAT_WITH_HOPPER, (manager, context) -> new BoatWithBlockRenderer(manager, Blocks.HOPPER.getDefaultState()));

        if (Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn)
            EntityRendererRegistry.INSTANCE.register(EntityTypeRegistry.ANVIL_MINECART_ENTITY, (manager, context) -> new MinecartEntityRenderer<>(manager));
        if (Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn)
            EntityRendererRegistry.INSTANCE.register(EntityTypeRegistry.NOTEBLOCK_MINECART_ENTITY, (manager, context) -> new MinecartEntityRenderer<>(manager));
        if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn)
            EntityRendererRegistry.INSTANCE.register(EntityTypeRegistry.JUKEBOX_MINECART_ENTITY, (manager, context) -> new MinecartEntityRenderer<>(manager));
    }
}
