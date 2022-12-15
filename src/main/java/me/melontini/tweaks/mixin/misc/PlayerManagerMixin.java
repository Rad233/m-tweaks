package me.melontini.tweaks.mixin.misc;

import me.melontini.tweaks.Tweaks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.melontini.tweaks.util.MiscUtil.generateRecipeAdvancements;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;", ordinal = 0, shift = At.Shift.BEFORE), method = "onDataPacksReloaded")
    private void reload(CallbackInfo ci) {
        //we don't sync until our advancements have been generated
        if (Tweaks.CONFIG.autogenRecipeAdvancements.autogenRecipeAdvancements) generateRecipeAdvancements(server);
    }
}
