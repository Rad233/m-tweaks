package me.melontini.tweaks.mixin.misc.unknown.nice_level;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@MixinRelatedConfigOption("unknown")
@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {

    @ModifyReturnValue(at = @At("RETURN"), method = "getPercentage")
    private String mTweaks$getPercentage(String o) {
        if (Tweaks.CONFIG.unknown) {
            if (Objects.equals(o, "69%")) {
                return "Nice%";
            }
        }
        return o;
    }
}
