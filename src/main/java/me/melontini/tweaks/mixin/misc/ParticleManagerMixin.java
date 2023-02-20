package me.melontini.tweaks.mixin.misc;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.client.particles.KnockoffTotemParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Shadow public abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleManager.SpriteAwareFactory<T> factory);

    @Inject(at = @At("TAIL"), method = "registerDefaultFactories")
    private void registerMTweaksFactories(CallbackInfo ci) {
        this.registerFactory(Tweaks.KNOCKOFF_TOTEM_PARTICLE, KnockoffTotemParticle.Factory::new);
    }
}
