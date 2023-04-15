package me.melontini.tweaks.mixin.misc.bye_adventure;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(GameModeSelectionScreen.class)
@MixinRelatedConfigOption("noMoreAdventure")
public abstract class GameModeSelectionScreenMixin extends Screen {
    protected GameModeSelectionScreenMixin(Text title) {
        super(title);
    }

    private final GameModeSelectionScreen.GameModeSelection[] mTweaks$gameModeSelections = ArrayUtils.removeElement(GameModeSelectionScreen.GameModeSelection.values(), GameModeSelectionScreen.GameModeSelection.ADVENTURE);

    @ModifyExpressionValue(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;VALUES:[Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;"))
    private GameModeSelectionScreen.GameModeSelection[] mTweaks$modValues(GameModeSelectionScreen.GameModeSelection[] original) {
        if (Tweaks.CONFIG.noMoreAdventure) {
            return mTweaks$gameModeSelections;
        }
        return original;
    }

    @ModifyExpressionValue(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen;UI_WIDTH:I"))
    private int mTweaks$modValues(int original) {
        if (Tweaks.CONFIG.noMoreAdventure) {
            return mTweaks$gameModeSelections.length * 31 - 5;
        }
        return original;
    }

    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameModeSelectionScreen$GameModeSelection;next()Ljava/util/Optional;"))
    private Optional<GameModeSelectionScreen.GameModeSelection> mTweaks$modNext(GameModeSelectionScreen.GameModeSelection selection, Operation<Optional<GameModeSelectionScreen.GameModeSelection>> operation) {
        if (Tweaks.CONFIG.noMoreAdventure) {
            return next(selection);
        }
        return operation.call(selection);
    }


    private static Optional<GameModeSelectionScreen.GameModeSelection> next(GameModeSelectionScreen.GameModeSelection selection) {
        switch (selection) {
            case CREATIVE:
                return Optional.of(GameModeSelectionScreen.GameModeSelection.SURVIVAL);
            case SURVIVAL, ADVENTURE:
                return Optional.of(GameModeSelectionScreen.GameModeSelection.SPECTATOR);
            default:
                return Optional.of(GameModeSelectionScreen.GameModeSelection.CREATIVE);
        }
    }
}
