package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import me.melontini.tweaks.util.data.PlantData;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinRelatedConfigOption("temperatureBasedCropGrowthSpeed")
@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin {
    @Shadow public abstract void generate(ServerWorld world, BlockPos pos, BlockState state, Random random);

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        SaplingBlock block = (SaplingBlock) (Object) this;
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            PlantData data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId(block));
            if (data != null) {
                float temp = world.getBiome(pos).value().getTemperature();
                if (temp >= data.min && temp <= data.max) {
                    if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
                        this.generate(world, pos, state, random);
                    }
                } else if ((temp > data.max && temp <= data.aMax) || (temp < data.min && temp >= data.aMin)) {
                    if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(18) == 0) {
                        this.generate(world, pos, state, random);
                    }
                }
                ci.cancel();
            }
        }
    }
}
