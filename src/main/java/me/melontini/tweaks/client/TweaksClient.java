package me.melontini.tweaks.client;

import me.melontini.tweaks.client.render.BoatWithBlockRenderer;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.networks.ClientSideNetworking;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class TweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();

        ClientSideNetworking.register();

        if (config.newBoats.isFurnaceBoatOn)
            EntityRendererRegistry.register(EntityTypeRegistry.BOAT_WITH_FURNACE, (ctx -> new BoatWithBlockRenderer(ctx, Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.WEST))));
        if (config.newBoats.isJukeboxBoatOn)
            EntityRendererRegistry.register(EntityTypeRegistry.BOAT_WITH_JUKEBOX, (ctx -> new BoatWithBlockRenderer(ctx, Blocks.JUKEBOX.getDefaultState())));
        if (config.newBoats.isTNTBoatOn)
            EntityRendererRegistry.register(EntityTypeRegistry.BOAT_WITH_TNT, (ctx -> new BoatWithBlockRenderer(ctx, Blocks.TNT.getDefaultState())));

        if (config.newMinecarts.isAnvilMinecartOn)
            EntityRendererRegistry.register(EntityTypeRegistry.ANVIL_MINECART_ENTITY, (ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.MINECART)));
        if (config.newMinecarts.isNoteBlockMinecartOn)
            EntityRendererRegistry.register(EntityTypeRegistry.NOTEBLOCK_MINECART_ENTITY, (ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.MINECART)));
        if (config.newMinecarts.isJukeboxMinecartOn)
            EntityRendererRegistry.register(EntityTypeRegistry.JUKEBOX_MINECART_ENTITY, (ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.MINECART)));
    }
}
