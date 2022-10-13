package me.melontini.tweaks.mixin.misc.smooth_tooltips;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@MixinRelatedConfigOption("enableSmoothTooltips")
@Mixin(Screen.class)
public abstract class ScreenMixin {

    private float mTweaks$oldTime, mTweaks$newTime, mTweaks$delta;
    private float mTweaks$aFloat, mTweaks$aFloat2;

    @Shadow
    protected abstract void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y);

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"), method = "renderOrderedTooltip")
    private void mTweaks$tooltip(Screen instance, MatrixStack matrices, List<TooltipComponent> components, int mouseX, int mouseY) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            mTweaks$oldTime = mTweaks$newTime;
            mTweaks$newTime = Util.getMeasuringTimeMs();
            mTweaks$delta = mTweaks$newTime - mTweaks$oldTime;

            mTweaks$aFloat = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * mTweaks$delta, mTweaks$aFloat, mouseX), mouseX - 30, mouseX + 30);
            mTweaks$aFloat2 = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * mTweaks$delta, mTweaks$aFloat2, mouseY), mouseY - 30, mouseY + 30);

            MatrixStack matrixStack1 = RenderSystem.getModelViewStack();
            matrixStack1.push();
            matrixStack1.translate(mTweaks$aFloat - (int) (mTweaks$aFloat), mTweaks$aFloat2 - (int) (mTweaks$aFloat2), 0);
            RenderSystem.applyModelViewMatrix();
            renderTooltipFromComponents(matrices, components, (int) (mTweaks$aFloat), (int) (mTweaks$aFloat2));
            matrixStack1.pop();
            RenderSystem.applyModelViewMatrix();
        } else renderTooltipFromComponents(matrices, components, mouseX, mouseY);
    }


    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V")
    private void mTweaks$tooltip2(Screen instance, MatrixStack matrices, List<TooltipComponent> components, int mouseX, int mouseY) {
        if (Tweaks.CONFIG.enableSmoothTooltips) {
            mTweaks$oldTime = mTweaks$newTime;
            mTweaks$newTime = Util.getMeasuringTimeMs();
            mTweaks$delta = mTweaks$newTime - mTweaks$oldTime;

            mTweaks$aFloat = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * mTweaks$delta, mTweaks$aFloat, mouseX), mouseX - 30, mouseX + 30);
            mTweaks$aFloat2 = MathHelper.clamp(MathHelper.lerp(Tweaks.CONFIG.tooltipMultiplier * mTweaks$delta, mTweaks$aFloat2, mouseY), mouseY - 30, mouseY + 30);

            MatrixStack matrixStack1 = RenderSystem.getModelViewStack();
            matrixStack1.push();
            matrixStack1.translate(mTweaks$aFloat - (int) (mTweaks$aFloat), mTweaks$aFloat2 - (int) (mTweaks$aFloat2), 0);
            RenderSystem.applyModelViewMatrix();
            renderTooltipFromComponents(matrices, components, (int) (mTweaks$aFloat), (int) (mTweaks$aFloat2));
            matrixStack1.pop();
            RenderSystem.applyModelViewMatrix();
        } else renderTooltipFromComponents(matrices, components, mouseX, mouseY);
    }
}
