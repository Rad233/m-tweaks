package me.melontini.tweaks.mixin.crop_temperature;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SaplingBlock.class)
public class SaplingBlockMixin {
    //TODO this should probably be tag based
    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void mTweaks$tick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        SaplingBlock block = (SaplingBlock) (Object) this;
        if (Tweaks.CONFIG.cropsGrowSlowerInCold) {
            float temp = world.getBiome(pos).getTemperature();
            if (temp > 0 && temp < 0.6) {
                //LogUtil.info("cold " + temp);
                if (world.getRandom().nextInt((int) (25 / (6 * (temp + 0.2)))) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                    block.generate(world, pos, state, random);
                }
            } else if (temp >= 0.6) {
                //LogUtil.info("normal " + temp);
                if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
                    block.generate(world, pos, state, random);
                }
            }
            ci.cancel();
        }
    }
}
