package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.data.PlantData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CocoaBlock.class)
public class CocoaBlockMixin {
    @Shadow @Final public static IntProperty AGE;

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    private void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            PlantData data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId((CocoaBlock) (Object) this));
            if (data != null) {
                float temp = world.getBiome(pos).value().getTemperature();
                int i = state.get(AGE);
                if (temp >= data.min && temp <= data.max) {
                    if (world.random.nextInt(5) == 0) {
                        if (i < 2) {
                            world.setBlockState(pos, state.with(AGE, i + 1), Block.NOTIFY_LISTENERS);
                        }
                    }
                } else if ((temp > data.max && temp <= data.aMax) && (temp < data.min && temp >= data.aMin)) {
                    if (world.random.nextInt(10) == 0) {
                        if (i < 2) {
                            world.setBlockState(pos, state.with(AGE, i + 1), Block.NOTIFY_LISTENERS);
                        }
                    }
                }
                ci.cancel();
            }
        }
    }
}
