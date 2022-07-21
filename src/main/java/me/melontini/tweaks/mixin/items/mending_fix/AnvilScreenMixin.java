package me.melontini.tweaks.mixin.items.mending_fix;

import me.melontini.tweaks.Tweaks;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @ModifyConstant(method = "drawForeground", constant = @Constant(intValue = 40))
    private int updateResult(int constant) {
        return Tweaks.CONFIG.balancedMending ? Integer.MAX_VALUE : constant;
    }
}
