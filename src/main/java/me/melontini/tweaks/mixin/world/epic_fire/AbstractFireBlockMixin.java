package me.melontini.tweaks.mixin.world.epic_fire;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@MixinRelatedConfigOption("quickFire")
@Mixin(FireBlock.class)
public abstract class AbstractFireBlockMixin extends AbstractFireBlock {
    public AbstractFireBlockMixin(Settings settings, float damage) {
        super(settings, damage);
    }

    @Shadow
    protected abstract int getSpreadChance(BlockState state);

    @Shadow
    protected abstract BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age);

    @Shadow
    protected abstract void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge);

    @Inject(at = @At("HEAD"), method = "trySpreadingFire", cancellable = true)
    public void mTweaks$spreadFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge, CallbackInfo ci) {
        if (Tweaks.CONFIG.quickFire) {
            int i = this.getSpreadChance(world.getBlockState(pos));
            if (random.nextInt((int) (spreadFactor * 0.8)) < i) {
                BlockState blockState = world.getBlockState(pos);
                if (random.nextInt(currentAge + 4) < 5 && !world.hasRain(pos)) {
                    int j = Math.min(currentAge + random.nextInt(5) / 4, 15);
                    world.setBlockState(pos, this.getStateWithAge(world, pos, j), Block.NOTIFY_ALL);
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
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/FireBlock.trySpreadingFire (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILjava/util/Random;I)V", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "scheduledTick")
    public void mTweaks$trySpreadBlocks(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci, int i, boolean bl2, int k) {
        if (Tweaks.CONFIG.quickFire) {
            for (int x = -3; x < 3; x++) {
                for (int y = -3; y < 3; y++) {
                    for (int z = -3; z < 3; z++) {
                        this.trySpreadingFire(world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), 300 + k, random, i);
                    }
                }
            }
        }
    }
}
