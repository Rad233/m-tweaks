package me.melontini.tweaks.mixin.items.mending_fix;

import me.melontini.tweaks.Tweaks;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @ModifyConstant(method = "updateResult", constant = @Constant(intValue = 40, ordinal = 2))
    private int mTweaks$updateResult(int constant) {
        //based
        return Tweaks.CONFIG.balancedMending ? Integer.MAX_VALUE : constant;
    }
}
