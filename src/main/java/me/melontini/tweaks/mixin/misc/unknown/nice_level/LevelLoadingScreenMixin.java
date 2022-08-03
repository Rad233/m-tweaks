package me.melontini.tweaks.mixin.misc.unknown.nice_level;

import me.melontini.tweaks.Tweaks;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {
    @Shadow @Final private WorldGenerationProgressTracker progressProvider;

    @Inject(at = @At("RETURN"), method = "getPercentage", cancellable = true)
    private void mTweaks$getPercentage(CallbackInfoReturnable<String> cir) {
        if (Tweaks.CONFIG.unknown) {
            int a = MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100);
            String i = a != 69 ? a + "%" : "Nice";
            cir.setReturnValue(i);
        }
    }
}
