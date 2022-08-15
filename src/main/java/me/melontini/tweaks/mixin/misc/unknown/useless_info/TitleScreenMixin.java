package me.melontini.tweaks.mixin.misc.unknown.useless_info;

import me.melontini.tweaks.client.TweaksClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/SplashTextResourceSupplier;get()Ljava/lang/String;"), method = "init")
    private void mTweaks$init(CallbackInfo ci) {
        TweaksClient.TEXT = this.client.getSplashTextLoader().get();
    }
}
