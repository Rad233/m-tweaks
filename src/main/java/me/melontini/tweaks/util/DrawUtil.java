package me.melontini.tweaks.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DrawUtil {
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
}
