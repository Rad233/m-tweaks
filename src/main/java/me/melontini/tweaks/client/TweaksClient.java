package me.melontini.tweaks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.crackerutil.client.util.DrawUtil;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.render.BoatWithBlockRenderer;
import me.melontini.tweaks.client.render.block.IncubatorBlockRenderer;
import me.melontini.tweaks.client.screens.FletchingScreen;
import me.melontini.tweaks.networks.ClientSideNetworking;
import me.melontini.tweaks.registries.BlockRegistry;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.util.TweaksTexts;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class TweaksClient implements ClientModInitializer {

    public static String TEXT;
    public static ItemStack FRAME_STACK = ItemStack.EMPTY;
    private float tooltipFlow;

    @Override
    public void onInitializeClient() {
        ClientSideNetworking.register();
        registerEntityRenderers();
        registerBlockRenderers();

        inGameTooltips();

        if (Tweaks.CONFIG.usefulFletching)
            HandledScreens.register(Tweaks.FLETCHING_SCREEN_HANDLER, FletchingScreen::new);
    }

    private void inGameTooltips() {
        HudRenderCallback.EVENT.register((matrices, delta) -> {
            if (Tweaks.CONFIG.itemFrameTooltips) {
                var client = MinecraftClient.getInstance();
                var cast = client.crosshairTarget;

                getCast(cast);

                if (!FRAME_STACK.isEmpty()) {
                    tooltipFlow = MathHelper.lerp(0.25f * client.getLastFrameDuration(), tooltipFlow, 1);
                    matrices.push();
                    matrices.scale(1, 1, 1);
                    RenderSystem.setShaderColor(1, 1, 1, Math.min(tooltipFlow, 0.8f));
                    var list = DrawUtil.getTooltipFromItem(FRAME_STACK);
                    list.add(TweaksTexts.ITEM_IN_FRAME);
                    List<TooltipComponent> list1 = list.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
                    FRAME_STACK.getTooltipData().ifPresent(datax -> list1.add(1, TooltipComponent.of(datax)));

                    int j = 0;
                    for (TooltipComponent tooltipComponent : list1) {
                        j += tooltipComponent.getHeight();
                    }

                    DrawUtil.renderTooltipFromComponents(matrices, list1, (((client.getWindow().getScaledWidth() / 2f)) - (tooltipFlow * 15)) + 15, ((client.getWindow().getScaledHeight() - j) / 2f) + 12);
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    matrices.pop();
                } else {
                    tooltipFlow = MathHelper.lerp(0.1f * client.getLastFrameDuration(), tooltipFlow, 0);
                }
            }
        });
    }

    private void getCast(HitResult cast) {
        if (cast != null) if (cast.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitResult = (EntityHitResult) cast;
            if (hitResult.getEntity() instanceof ItemFrameEntity itemFrameEntity) {
                FRAME_STACK = itemFrameEntity.getHeldItemStack();
                return;
            }
        }
        FRAME_STACK = ItemStack.EMPTY;
    }

    public void registerBlockRenderers() {
        if (Tweaks.CONFIG.unknown)
            BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.ROSE_OF_THE_VALLEY);

        if (Tweaks.CONFIG.incubatorSettings.enableIncubator) {
            BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockRegistry.INCUBATOR_BLOCK);
            BlockEntityRendererRegistry.register(BlockRegistry.INCUBATOR_BLOCK_ENTITY, IncubatorBlockRenderer::new);
        }
    }

    public void registerEntityRenderers() {
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
    }
}
