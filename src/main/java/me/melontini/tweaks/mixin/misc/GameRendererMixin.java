package me.melontini.tweaks.mixin.misc;

import me.melontini.tweaks.client.TweaksClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;skipGameRender:Z", shift = At.Shift.AFTER), method = "render")
    private void setFrameDelta(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        TweaksClient.OLD_TIME = TweaksClient.NEW_TIME;
        TweaksClient.NEW_TIME = Util.getMeasuringTimeMs();
        TweaksClient.DELTA = TweaksClient.NEW_TIME - TweaksClient.OLD_TIME;
    }
}
