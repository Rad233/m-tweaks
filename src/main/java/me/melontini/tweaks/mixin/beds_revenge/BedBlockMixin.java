package me.melontini.tweaks.mixin.beds_revenge;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.config.TweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BedBlock.isBedWorking;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin extends Block {

    public BedBlockMixin(Settings settings) {
        super(settings);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"), method = "onUse")
    public Explosion mTweaks$explosionRedirect(@NotNull World instance, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType) {
        float explosionPower = Tweaks.CONFIG.bedExplosionPower;
        Explosion explosion = new Explosion(instance, entity, damageSource, behavior, x, y, z, explosionPower, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        return explosion;
    }

    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    public void mTweaks$onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient) {
            TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
            if (config.bedsExplodeEverywhere) {
                float explosionPower = config.bedExplosionPower;
                world.removeBlock(pos, false);
                BlockPos blockPos = pos.offset(state.get(BedBlock.FACING).getOpposite());
                if (world.getBlockState(blockPos).isOf(this)) {
                    world.removeBlock(blockPos, false);
                }
                world.createExplosion(
                        null,
                        DamageSource.badRespawnPoint(),
                        null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        explosionPower,
                        true,
                        Explosion.DestructionType.DESTROY
                );
                cir.setReturnValue(ActionResult.SUCCESS);
            }
            if (config.safeBeds) {
                if (!isBedWorking(world)) {
                    player.sendMessage(Text.translatable("m-tweaks.safebeds.action"), true);
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }
}
