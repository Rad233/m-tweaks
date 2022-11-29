package me.melontini.tweaks.mixin.blocks.beds_revenge;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.TextUtil;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BedBlock.isBedWorking;
import static net.minecraft.block.HorizontalFacingBlock.FACING;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin extends Block {

    public BedBlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;Lnet/minecraft/util/math/Vec3d;FZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"), index = 4, method = "onUse")
    public float mTweaks$explosionRedirect(float power) {
        return Tweaks.CONFIG.bedExplosionPower;
    }


    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    private void mTweaks$alwaysExplode(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (Tweaks.CONFIG.bedsExplodeEverywhere) {
            world.removeBlock(pos, false);
            BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
            if (world.getBlockState(blockPos).isOf(this)) {
                world.removeBlock(blockPos, false);
            }

            world.createExplosion(
                    null,
                    DamageSource.badRespawnPoint(pos.toCenterPos()),
                    null,
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    Tweaks.CONFIG.bedExplosionPower,
                    true,
                    World.ExplosionSourceType.BLOCK
            );
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    public void mTweaks$onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient) {
            if (Tweaks.CONFIG.safeBeds) {
                if (!isBedWorking(world)) {
                    player.sendMessage(TextUtil.createTranslatable("m-tweaks.safebeds.action"), true);
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }
}
