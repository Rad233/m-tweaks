package me.melontini.tweaks.client;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.render.BoatWithBlockRenderer;
import me.melontini.tweaks.client.screens.FletchingScreen;
import me.melontini.tweaks.networks.ClientSideNetworking;
import me.melontini.tweaks.registries.BlockRegistry;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TweaksClient implements ClientModInitializer {

    public static List<String> USEFUL_TEXTS = new ArrayList<>();
    @Override
    public void onInitializeClient() {
        USEFUL_TEXTS.addAll(List.of(
                "Hi",
                "This is Like Splashes, but Worse!",
                "Cheating Much?",
                "I Have ALL the Info! hehe",
                "Fabric",
                "Luv U!",
                "Maps a for Losers",
                "Free Ad Space!",
                MinecraftClient.getInstance().getSession().getUsername() + " is You!",
                "More Than One Message!",
                "Reminder",
                "???"
        ));

        ClientSideNetworking.register();

        if (Tweaks.CONFIG.usefulFletching) HandledScreens.register(Tweaks.FLETCHING_SCREEN_HANDLER, FletchingScreen::new);

        if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
            EntityRendererRegistry.register(EntityTypeRegistry.BOAT_WITH_FURNACE, (ctx -> new BoatWithBlockRenderer(ctx, Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH))));
        if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
            EntityRendererRegistry.register(EntityTypeRegistry.BOAT_WITH_JUKEBOX, (ctx -> new BoatWithBlockRenderer(ctx, Blocks.JUKEBOX.getDefaultState())));
        if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
            EntityRendererRegistry.register(EntityTypeRegistry.BOAT_WITH_TNT, (ctx -> new BoatWithBlockRenderer(ctx, Blocks.TNT.getDefaultState())));
        if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
            EntityRendererRegistry.register(EntityTypeRegistry.BOAT_WITH_HOPPER, (ctx -> new BoatWithBlockRenderer(ctx, Blocks.HOPPER.getDefaultState())));

        if (Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn)
            EntityRendererRegistry.register(EntityTypeRegistry.ANVIL_MINECART_ENTITY, (ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.MINECART)));
        if (Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn)
            EntityRendererRegistry.register(EntityTypeRegistry.NOTEBLOCK_MINECART_ENTITY, (ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.MINECART)));
        if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn)
            EntityRendererRegistry.register(EntityTypeRegistry.JUKEBOX_MINECART_ENTITY, (ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.MINECART)));


        if (Tweaks.CONFIG.unknown)
            BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.ROSE_OF_THE_VALLEY);
    }
}
