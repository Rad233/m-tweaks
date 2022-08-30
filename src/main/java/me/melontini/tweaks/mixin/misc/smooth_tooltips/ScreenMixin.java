package me.melontini.tweaks.mixin.misc.smooth_tooltips;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@MixinRelatedConfigOption("enableSmoothTooltips")
@Mixin(Screen.class)
public abstract class ScreenMixin {
    private float mTweaks$aFloat = -10000;
    private float mTweaks$aFloat2 = -10000;
    private long mTweaks$oldTime;

    @Inject(at = @At("TAIL"), method = "init(Lnet/minecraft/client/MinecraftClient;II)V")
    private void mTweaks$init(MinecraftClient client, int width, int height, CallbackInfo ci) {
        mTweaks$aFloat = -10000;
        mTweaks$aFloat2 = -10000;
    }

    @Shadow
    protected abstract void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y);

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"), method = "renderOrderedTooltip")
    private void mTweaks$tooltip(Screen instance, MatrixStack matrices, List<TooltipComponent> components, int mouseX, int mouseY) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            if (Util.getMeasuringTimeMs() != mTweaks$oldTime) {
                this.mTweaks$oldTime = Util.getMeasuringTimeMs();
                if (mTweaks$aFloat == -10000) mTweaks$aFloat = mouseX;
                if (mTweaks$aFloat2 == -10000) mTweaks$aFloat2 = mouseY;

                if (!(mTweaks$aFloat <= mouseX + 0.1 && mTweaks$aFloat >= mouseX - 0.1)) {
                    if (this.mTweaks$aFloat < mouseX) {
                        this.mTweaks$aFloat = (float) MathHelper.clamp((this.mTweaks$aFloat + (Tweaks.CONFIG.tooltipMultiplier) * ((mouseX - mTweaks$aFloat) * 0.02)), mouseX - 30, mouseX + 30);
                    }
                    if (this.mTweaks$aFloat > mouseX) {
                        this.mTweaks$aFloat = (float) MathHelper.clamp((this.mTweaks$aFloat - (Tweaks.CONFIG.tooltipMultiplier) * ((mTweaks$aFloat - mouseX) * 0.02)), mouseX - 30, mouseX + 30);
                    }
                }

                if (!(mTweaks$aFloat2 <= mouseY + 0.1 && mTweaks$aFloat2 >= mouseY - 0.1)) {
                    if (this.mTweaks$aFloat2 < mouseY) {
                        this.mTweaks$aFloat2 = (float) MathHelper.clamp((this.mTweaks$aFloat2 + (Tweaks.CONFIG.tooltipMultiplier) * ((mouseY - mTweaks$aFloat2) * 0.02)), mouseY - 30, mouseY + 30);
                    }
                    if (this.mTweaks$aFloat2 > mouseY) {
                        this.mTweaks$aFloat2 = (float) MathHelper.clamp((this.mTweaks$aFloat2 - (Tweaks.CONFIG.tooltipMultiplier) * ((mTweaks$aFloat2 - mouseY) * 0.02)), mouseY - 30, mouseY + 30);
                    }
                }
            }
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.translate(mTweaks$aFloat, mTweaks$aFloat2, 0);
            renderTooltipFromComponents(matrixStack, components, 0, 0);
        } else renderTooltipFromComponents(matrices, components, mouseX, mouseY);
    }


    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V")
    private void mTweaks$tooltip2(Screen instance, MatrixStack matrices, List<TooltipComponent> components, int mouseX, int mouseY) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            if (Util.getMeasuringTimeMs() != mTweaks$oldTime) {
                this.mTweaks$oldTime = Util.getMeasuringTimeMs();
                if (mTweaks$aFloat == -10000) mTweaks$aFloat = mouseX;
                if (mTweaks$aFloat2 == -10000) mTweaks$aFloat2 = mouseY;

                if (!(mTweaks$aFloat <= mouseX + 0.1 && mTweaks$aFloat >= mouseX - 0.1)) {
                    if (this.mTweaks$aFloat < mouseX) {
                        this.mTweaks$aFloat = (float) MathHelper.clamp((this.mTweaks$aFloat + (Tweaks.CONFIG.tooltipMultiplier) * ((mouseX - mTweaks$aFloat) * 0.02)), mouseX - 30, mouseX + 30);
                    }
                    if (this.mTweaks$aFloat > mouseX) {
                        this.mTweaks$aFloat = (float) MathHelper.clamp((this.mTweaks$aFloat - (Tweaks.CONFIG.tooltipMultiplier) * ((mTweaks$aFloat - mouseX) * 0.02)), mouseX - 30, mouseX + 30);
                    }
                }

                if (!(mTweaks$aFloat2 <= mouseY + 0.1 && mTweaks$aFloat2 >= mouseY - 0.1)) {
                    if (this.mTweaks$aFloat2 < mouseY) {
                        this.mTweaks$aFloat2 = (float) MathHelper.clamp((this.mTweaks$aFloat2 + (Tweaks.CONFIG.tooltipMultiplier) * ((mouseY - mTweaks$aFloat2) * 0.02)), mouseY - 30, mouseY + 30);
                    }
                    if (this.mTweaks$aFloat2 > mouseY) {
                        this.mTweaks$aFloat2 = (float) MathHelper.clamp((this.mTweaks$aFloat2 - (Tweaks.CONFIG.tooltipMultiplier) * ((mTweaks$aFloat2 - mouseY) * 0.02)), mouseY - 30, mouseY + 30);
                    }
                }
            }
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.translate(mTweaks$aFloat, mTweaks$aFloat2, 0);
            renderTooltipFromComponents(matrixStack, components, 0, 0);
        } else renderTooltipFromComponents(matrices, components, mouseX, mouseY);
    }
}
