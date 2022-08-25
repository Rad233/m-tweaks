package me.melontini.tweaks.mixin.misc.unknown.nice_level;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@MixinRelatedConfigOption("unknown")
@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {
    @Inject(at = @At("RETURN"), method = "getPercentage", cancellable = true)
    private void mTweaks$getPercentage(CallbackInfoReturnable<String> cir) {
        if (Tweaks.CONFIG.unknown) {
            if (Objects.equals(cir.getReturnValue(), "69%")) {
                cir.setReturnValue("Nice%");
            }
        }
    }
}
