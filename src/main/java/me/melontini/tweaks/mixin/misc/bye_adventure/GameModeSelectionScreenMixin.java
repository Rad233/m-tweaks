package me.melontini.tweaks.mixin.misc.bye_adventure;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameModeSelectionScreen.class)
@MixinRelatedConfigOption("noMoreAdventure")
public abstract class GameModeSelectionScreenMixin extends Screen {
    protected GameModeSelectionScreenMixin(Text title) {
        super(title);
    }

    private final GameModeSelectionScreen.GameModeSelection[] mTweaks$gameModeSelections = ArrayUtils.removeElement(GameModeSelectionScreen.GameModeSelection.values(), GameModeSelectionScreen.GameModeSelection.ADVENTURE);

    @ModifyExpressionValue(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;VALUES:[Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;"))
    private GameModeSelectionScreen.GameModeSelection[] mTweaks$modValues(GameModeSelectionScreen.GameModeSelection[] original) {
        return !Tweaks.CONFIG.noMoreAdventure ? original : mTweaks$gameModeSelections;
    }

    @ModifyExpressionValue(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen;UI_WIDTH:I"))
    private int mTweaks$modValues(int original) {
        return !Tweaks.CONFIG.noMoreAdventure ? original : mTweaks$gameModeSelections.length * 31 - 5;
    }
}
