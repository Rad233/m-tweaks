package me.melontini.tweaks.mixin.misc.minor_inconvenience;

import me.melontini.tweaks.Tweaks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.damage (Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.BEFORE), method = "damage", cancellable = true)
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (Tweaks.CONFIG.minorInconvenience && !world.isClient) {
            super.damage(DamageSource.badRespawnPoint(), Float.MAX_VALUE);
            this.world.createExplosion(null, DamageSource.badRespawnPoint(), null, this.getBlockX() + 0.5, this.getBlockY() + 0.5, this.getBlockZ() + 0.5, 5.0F, true, Explosion.DestructionType.DESTROY);
            cir.setReturnValue(true);
        }
    }
}
