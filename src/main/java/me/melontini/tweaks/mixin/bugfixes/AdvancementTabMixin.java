package me.melontini.tweaks.mixin.bugfixes;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@MixinRelatedConfigOption("frameIndependentAdvancementShadow")
@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {
    @Shadow private float alpha;

    @Shadow @Final private MinecraftClient client;

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 0), index = 0, method = "drawWidgetTooltip")
    private float mTweaks$draw(float value) {
        return Tweaks.CONFIG.frameIndependentAdvancementShadow ? this.alpha + (0.04F * client.getLastFrameDuration()) : value;
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 1), index = 0, method = "drawWidgetTooltip")
    private float mTweaks$draw1(float value) {
        return Tweaks.CONFIG.frameIndependentAdvancementShadow ? this.alpha - (0.06F * client.getLastFrameDuration()) : value;
    }
}
