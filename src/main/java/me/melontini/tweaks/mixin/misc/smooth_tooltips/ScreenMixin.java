package me.melontini.tweaks.mixin.misc.smooth_tooltips;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@MixinRelatedConfigOption("enableSmoothTooltips")
@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    @Nullable
    protected MinecraftClient client;
    @Shadow
    protected TextRenderer textRenderer;
    private float mTweaks$smoothX, mTweaks$smoothY;

    @Inject(method = "renderTooltipFromComponents", at = @At("HEAD"))
    private void mTweaks$renderTooltipHead(MatrixStack matrices, List<TooltipComponent> components, int x, int y, CallbackInfo ci) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
            float smoothX = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * client.getLastFrameDuration(), mTweaks$smoothX, x), x - 30, x + 30);
            float smoothY = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * client.getLastFrameDuration(), mTweaks$smoothY, y), y - 30, y + 30);

            int i = 0;
            int j = components.size() == 1 ? -2 : 0;
            for (TooltipComponent tooltipComponent : components) {
                int k = tooltipComponent.getWidth(textRenderer);
                if (k > i) {
                    i = k;
                }

                j += tooltipComponent.getHeight();
            }

            float l = smoothX + 12;
            float m = smoothY - 12;
            if (l + i > width) l -= 28 + i;
            mTweaks$smoothX = smoothX;


            if (m + j + 6 > height) m = height - j - 6;
            mTweaks$smoothY = smoothY;


            RenderSystem.getModelViewStack().push();
            RenderSystem.getModelViewStack().translate(
                    l - (int)l,
                    m - (int)m, 0);
            RenderSystem.applyModelViewMatrix();
        }
    }

    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "LOAD"), index = 3, argsOnly = true)
    private int mTweaks$setX(int value) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            return (int) mTweaks$smoothX;
        }
        return value;
    }

    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "LOAD"), index = 4, argsOnly = true)
    private int mTweaks$setY(int value) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            return (int) mTweaks$smoothY;
        }
        return value;
    }

    @Inject(method = "renderTooltipFromComponents", at = @At("TAIL"))
    private void mTweaks$renderTooltipTail(MatrixStack matrices, List<TooltipComponent> components, int x, int y, CallbackInfo ci) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            RenderSystem.getModelViewStack().pop();
            RenderSystem.applyModelViewMatrix();
        }
    }
}