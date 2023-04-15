package me.melontini.tweaks.mixin.misc.bye_adventure;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameModeSelectionScreen.GameModeSelection.class)
@MixinRelatedConfigOption("noMoreAdventure")
public class GameModeSelectionMixin {
    @ModifyExpressionValue(method = "next", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;ADVENTURE:Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;"))
    private GameModeSelectionScreen.GameModeSelection mTweaks$next(GameModeSelectionScreen.GameModeSelection original) {
        return !Tweaks.CONFIG.noMoreAdventure ? original : GameModeSelectionScreen.GameModeSelection.SPECTATOR;
    }
}
