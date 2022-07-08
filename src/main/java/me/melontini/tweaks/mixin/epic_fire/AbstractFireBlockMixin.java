package me.melontini.tweaks.mixin.epic_fire;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FireBlock.class)
public abstract class AbstractFireBlockMixin extends AbstractFireBlock {
    public AbstractFireBlockMixin(Settings settings, float damage) {
        super(settings, damage);
    }

    @Inject(at = @At("HEAD"), method = "trySpreadingFire", cancellable = true)
    public void mTweaks$spreadFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge, CallbackInfo ci) {
        if (Tweaks.CONFIG.quickFire) {
            FireBlock fireBlock = (FireBlock) (Object) this;
            int i = fireBlock.getSpreadChance(world.getBlockState(pos));
            if (random.nextInt((int) (spreadFactor * 0.8)) < i) {
                BlockState blockState = world.getBlockState(pos);
                if (random.nextInt(currentAge + 4) < 5 && !world.hasRain(pos)) {
                    int j = Math.min(currentAge + random.nextInt(5) / 4, 15);
                    world.setBlockState(pos, fireBlock.getStateWithAge(world, pos, j), Block.NOTIFY_ALL);
                } else {
                    world.removeBlock(pos, false);
                }

                Block block = blockState.getBlock();
                if (block instanceof TntBlock) {
                    TntBlock.primeTnt(world, pos);
                }
            }
            ci.cancel();
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/FireBlock.trySpreadingFire (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/util/math/random/Random;I)V", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "scheduledTick")
    public void mTweaks$trySpreadBlocks(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci, int i, boolean bl2, int k) {
        if (Tweaks.CONFIG.quickFire) {
            FireBlock fireBlock = (FireBlock) (Object) this;
            for (int x = -3; x < 3; x++) {
                for (int y = -3; y < 3; y++) {
                    for (int z = -3; z < 3; z++) {
                        fireBlock.trySpreadingFire(world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), 300 + k, random, i);
                    }
                }
            }
        }
    }
}
