package me.melontini.tweaks.mixin.misc.smooth_tooltips;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.TweaksClient;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@MixinRelatedConfigOption("enableSmoothTooltips")
@Mixin(Screen.class)
public abstract class ScreenMixin {
    private float mTweaks$smoothX, mTweaks$smoothY;

    @Inject(method = "renderTooltipFromComponents", at = @At("HEAD"))
    private void mTweaks$renderTooltipHead(MatrixStack matrices, List<TooltipComponent> components, int x, int y, CallbackInfo ci) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            mTweaks$smoothX = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * TweaksClient.DELTA, mTweaks$smoothX, x), x - 30, x + 30);
            mTweaks$smoothY = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * TweaksClient.DELTA, mTweaks$smoothY, y), y - 30, y + 30);

            MatrixStack matrixStack1 = RenderSystem.getModelViewStack();
            matrixStack1.push();
            matrixStack1.translate(mTweaks$smoothX - (int) (mTweaks$smoothX), mTweaks$smoothY - (int) (mTweaks$smoothY), 0);
            RenderSystem.applyModelViewMatrix();
        }
    }

    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "LOAD"), index = 3, argsOnly = true)
    private int mTweaks$setX(int value) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            return (int) (mTweaks$smoothX);
        }
        return value;
    }

    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "LOAD"), index = 4, argsOnly = true)
    private int mTweaks$setY(int value) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            return (int) (mTweaks$smoothY);
        }
        return value;
    }

    @Inject(method = "renderTooltipFromComponents", at = @At("TAIL"))
    private void mTweaks$renderTooltipTail(MatrixStack matrices, List<TooltipComponent> components, int x, int y, CallbackInfo ci) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            MatrixStack matrixStack1 = RenderSystem.getModelViewStack();
            matrixStack1.pop();
            RenderSystem.applyModelViewMatrix();
        }
    }
}
