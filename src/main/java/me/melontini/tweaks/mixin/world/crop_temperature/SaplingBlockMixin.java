package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaplingBlock.class)
public class SaplingBlockMixin {
    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void mTweaks$tick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        SaplingBlock block = (SaplingBlock) (Object) this;
        if (Tweaks.CONFIG.cropsGrowSlowerInCold) {
            float temp = world.getBiome(pos).value().getTemperature();
            var data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId(block));
            if (data != null) {
                var rand = temp < 1.0D ? (25 / (6 * (temp + 0.2))) : (25 / (6 / (temp - 0.2)));
                if (temp >= data.min && temp <= data.max) {
                    if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
                        block.generate(world, pos, state, random);
                    }
                } else if (temp > data.max && temp <= data.aMax) {
                    if (world.getRandom().nextInt((int) rand) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                        block.generate(world, pos, state, random);
                    }
                } else if (temp < data.min && temp >= data.aMin) {
                    if (world.getRandom().nextInt((int) rand) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                        block.generate(world, pos, state, random);
                    }
                }
            } else {
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
            }
            ci.cancel();
        }
    }
}
