package me.melontini.tweaks.mixin.items.mending_fix;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinRelatedConfigOption("balancedMending")
@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbMixin {
    @Inject(at = @At("HEAD"), method = "repairPlayerGears", cancellable = true)
    private void mTweaks$repair(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir) {
        if (Tweaks.CONFIG.balancedMending) cir.setReturnValue(amount);
    }
}
