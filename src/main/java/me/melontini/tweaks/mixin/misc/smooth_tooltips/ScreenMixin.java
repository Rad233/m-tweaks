package me.melontini.tweaks.mixin.misc.smooth_tooltips;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.DrawUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@MixinRelatedConfigOption("enableSmoothTooltips")
@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    @Nullable
    protected MinecraftClient client;
    private float mTweaks$smoothX, mTweaks$smoothY;

    @Inject(method = "renderTooltipFromComponents", at = @At("HEAD"), cancellable = true)
    private void mTweaks$renderTooltipHead(MatrixStack matrices, List<TooltipComponent> components, int x, int y, CallbackInfo ci) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            float smoothX = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * client.getLastFrameDuration(), mTweaks$smoothX, x), x - 30, x + 30);
            float smoothY = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * client.getLastFrameDuration(), mTweaks$smoothY, y), y - 30, y + 30);

            DrawUtil.renderTooltipFromComponents(matrices, components, smoothX, smoothY);
            ci.cancel();
            mTweaks$smoothX = smoothX;
            mTweaks$smoothY = smoothY;
        }
    }
}
