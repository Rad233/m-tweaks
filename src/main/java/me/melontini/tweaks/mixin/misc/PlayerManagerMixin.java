package me.melontini.tweaks.mixin.misc;

import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;reload(Lnet/minecraft/server/ServerAdvancementLoader;)V"), method = "onDataPacksReloaded")
    private void reload(PlayerAdvancementTracker instance, ServerAdvancementLoader advancementLoader) {
        //we don't sync until our advancements have been generated
    }
}
