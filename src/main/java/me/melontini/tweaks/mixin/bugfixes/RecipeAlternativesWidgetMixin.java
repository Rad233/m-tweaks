package me.melontini.tweaks.mixin.bugfixes;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeAlternativesWidget.class)
@MixinRelatedConfigOption("properlyAlignedRecipeAlternatives")
public class RecipeAlternativesWidgetMixin {
    @Shadow
    private int buttonX;

    @Shadow
    private int buttonY;

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeAlternativesWidget;renderGrid(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), index = 3, method = "render")
    private int mTweaks$prepareGrid(int i) {
        return Tweaks.CONFIG.properlyAlignedRecipeAlternatives ? 25 : i;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeAlternativesWidget;buttonX:I"), method = "renderGrid")
    private int mTweaks$renderGridX(RecipeAlternativesWidget instance) {
        return Tweaks.CONFIG.properlyAlignedRecipeAlternatives ? this.buttonX - 2 : this.buttonX;
    }

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeAlternativesWidget;buttonY:I"), method = "renderGrid")
    private int mTweaks$renderGridY(RecipeAlternativesWidget instance) {
        return Tweaks.CONFIG.properlyAlignedRecipeAlternatives ? this.buttonY - 1 : this.buttonY;
    }
}
