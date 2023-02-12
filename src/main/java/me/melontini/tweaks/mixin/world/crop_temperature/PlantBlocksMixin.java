package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import me.melontini.tweaks.util.data.PlantData;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinRelatedConfigOption("temperatureBasedCropGrowthSpeed")
@Pseudo
@Mixin(value = {AbstractPlantStemBlock.class, BambooBlock.class, CactusBlock.class, CocoaBlock.class, CropBlock.class, SaplingBlock.class, SugarCaneBlock.class, SweetBerryBushBlock.class})
public class PlantBlocksMixin {
    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            PlantData data = Tweaks.PLANT_DATA.get((Block) (Object) this);
            if (data != null) {
                float temp = world.getBiome(pos).value().getTemperature();
                if ((temp > data.max && temp <= data.aMax) || (temp < data.min && temp >= data.aMin)) {
                    if (random.nextInt(2) == 0) {
                        LogUtil.devInfo("cancel, {} grows slowly {}", ((Block) (Object) this).getName().toString(), temp);
                        ci.cancel();
                    }
                } else if ((temp > data.aMax) || (temp < data.aMin)) {
                    LogUtil.devInfo("cancel, {} won't grow {}", ((Block) (Object) this).getName().toString(), temp);
                    ci.cancel();
                }
            }
        }
    }
}
