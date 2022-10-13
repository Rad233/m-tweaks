package me.melontini.tweaks.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.particles.KnockoffTotemParticle;
import me.melontini.tweaks.client.render.BoatWithBlockRenderer;
import me.melontini.tweaks.client.render.block.IncubatorBlockRenderer;
import me.melontini.tweaks.client.screens.FletchingScreen;
import me.melontini.tweaks.networks.ClientSideNetworking;
import me.melontini.tweaks.registries.BlockRegistry;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.util.TextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class TweaksClient implements ClientModInitializer {

    public static String TEXT;
    public static ItemStack FRAME_STACK = ItemStack.EMPTY;

    protected static void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        renderTooltip(matrices, getTooltipFromItem(stack), stack.getTooltipData(), x, y);
    }

    public static void renderTooltip(MatrixStack matrices, List<Text> lines, Optional<TooltipData> data, int x, int y) {
        List<TooltipComponent> list = lines.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
        data.ifPresent(datax -> list.add(1, TooltipComponent.of(datax)));
        renderTooltipFromComponents(matrices, list, x, y);
    }

    public static List<Text> getTooltipFromItem(ItemStack stack) {
        var client = MinecraftClient.getInstance();
        return stack.getTooltip(client.player, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
    }

    public static void renderTooltip(MatrixStack matrices, Text text, int x, int y) {
        renderOrderedTooltip(matrices, Arrays.asList(text.asOrderedText()), x, y);
    }

    public static void renderTooltip(MatrixStack matrices, List<Text> lines, int x, int y) {
        renderOrderedTooltip(matrices, Lists.transform(lines, Text::asOrderedText), x, y);
    }

    public static void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y) {
        renderTooltipFromComponents(matrices, lines.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y);
    }

    public static void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y) {
        var itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();

        if (!components.isEmpty()) {
            int i = 0;
            int j = components.size() == 1 ? -2 : 0;

            for (TooltipComponent tooltipComponent : components) {
                int k = tooltipComponent.getWidth(textRenderer);
                if (k > i) {
                    i = k;
                }

                j += tooltipComponent.getHeight();
            }

            int l = x + 12;
            int m = y - 12;
            if (l + i > width) {
                l -= 28 + i;
            }

            if (m + j + 6 > height) {
                m = height - j - 6;
            }


            matrices.push();
            float f = itemRenderer.zOffset;
            itemRenderer.zOffset = 400.0F;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            fillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + i + 3, m - 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 3, l + i + 3, m + j + 4, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m + j + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + j + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, l + i + 3, m - 3, l + i + 4, m + j + 3, 400, -267386864, -267386864);
            fillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + j + 3 - 1, 400, 1347420415, 1344798847);
            fillGradient(matrix4f, bufferBuilder, l + i + 2, m - 3 + 1, l + i + 3, m + j + 3 - 1, 400, 1347420415, 1344798847);
            fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m - 3 + 1, 400, 1347420415, 1347420415);
            fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 2, l + i + 3, m + j + 3, 400, 1344798847, 1344798847);
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            BufferRenderer.drawWithShader(bufferBuilder.end());
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrices.translate(0.0, 0.0, 400.0);
            int s = m;

            for (int t = 0; t < components.size(); ++t) {
                TooltipComponent tooltipComponent2 = components.get(t);
                tooltipComponent2.drawText(textRenderer, l, s, matrix4f, immediate);
                s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
            }

            immediate.draw();
            matrices.pop();
            s = m;

            for (int t = 0; t < components.size(); ++t) {
                TooltipComponent tooltipComponent2 = components.get(t);
                tooltipComponent2.drawItems(textRenderer, l, s, matrices, itemRenderer, 400);
                s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
            }

            itemRenderer.zOffset = f;
        }
    }

    public static void fillGradient(MatrixStack matrices, float startX, float startY, float endX, float endY, int colorStart, int colorEnd, float z) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void fillGradient(Matrix4f matrix, BufferBuilder builder, float startX, float startY, float endX, float endY, float z, int colorStart, int colorEnd) {
        float f = (float) (colorStart >> 24 & 0xFF) / 255.0F;
        float g = (float) (colorStart >> 16 & 0xFF) / 255.0F;
        float h = (float) (colorStart >> 8 & 0xFF) / 255.0F;
        float i = (float) (colorStart & 0xFF) / 255.0F;
        float j = (float) (colorEnd >> 24 & 0xFF) / 255.0F;
        float k = (float) (colorEnd >> 16 & 0xFF) / 255.0F;
        float l = (float) (colorEnd >> 8 & 0xFF) / 255.0F;
        float m = (float) (colorEnd & 0xFF) / 255.0F;
        builder.vertex(matrix, endX, startY, z).color(g, h, i, f).next();
        builder.vertex(matrix, startX, startY, z).color(g, h, i, f).next();
        builder.vertex(matrix, startX, endY, z).color(k, l, m, j).next();
        builder.vertex(matrix, endX, endY, z).color(k, l, m, j).next();
    }

    @Override
    public void onInitializeClient() {
        ClientSideNetworking.register();
        registerEntityRenderers();
        registerBlockRenderers();

        HudRenderCallback.EVENT.register((matrices, delta) -> {
            if (Tweaks.CONFIG.itemFrameTooltips) {
                if (!FRAME_STACK.isEmpty()) {
                    var client = MinecraftClient.getInstance();
                    matrices.push();
                    matrices.scale(1, 1, 1);
                    RenderSystem.setShaderColor(1, 1, 1, 0.75f);
                    var list = getTooltipFromItem(FRAME_STACK);
                    list.add(TextUtil.applyFormatting(TextUtil.createTranslatable("tooltip.m-tweaks.frameitem"), Formatting.GRAY));
                    List<TooltipComponent> list1 = list.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
                    FRAME_STACK.getTooltipData().ifPresent(datax -> list1.add(1, TooltipComponent.of(datax)));

                    int j = 0;
                    for (TooltipComponent tooltipComponent : list1) {
                        j += tooltipComponent.getHeight();
                    }

                    renderTooltipFromComponents(matrices, list1, client.getWindow().getScaledWidth() / 2, ((client.getWindow().getScaledHeight() - j) / 2) + 12);
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    matrices.pop();
                }
            }
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (Tweaks.CONFIG.itemFrameTooltips) {
                var client = MinecraftClient.getInstance();
                var cast = client.crosshairTarget;

                if (cast != null) if (cast.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult hitResult = (EntityHitResult) cast;
                    if (hitResult.getEntity() instanceof ItemFrameEntity itemFrameEntity) {
                        FRAME_STACK = itemFrameEntity.getHeldItemStack();
                        return;
                    }
                }

                FRAME_STACK = ItemStack.EMPTY;
            }
        });

        if (Tweaks.CONFIG.usefulFletching)
            HandledScreens.register(Tweaks.FLETCHING_SCREEN_HANDLER, FletchingScreen::new);

        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
            ParticleFactoryRegistry.getInstance().register(Tweaks.KNOCKOFF_TOTEM_PARTICLE, KnockoffTotemParticle.Factory::new);
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
