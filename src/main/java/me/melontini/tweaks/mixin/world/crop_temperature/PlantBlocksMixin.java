package me.melontini.tweaks.mixin.world.crop_temperature;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.crackerutil.util.Utilities;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import me.melontini.tweaks.util.data.PlantData;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinRelatedConfigOption("temperatureBasedCropGrowthSpeed")
@Pseudo
@Mixin(value = {AbstractPlantStemBlock.class, BambooBlock.class, CactusBlock.class, CocoaBlock.class, CropBlock.class, SaplingBlock.class, SugarCaneBlock.class, SweetBerryBushBlock.class})
public class PlantBlocksMixin {
    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true) //why? Because Mojang random
    private void randomTick(CallbackInfo ci, @Local(argsOnly = true, index = 1) BlockState state, @Local(argsOnly = true, index = 2) ServerWorld world, @Local(argsOnly = true, index = 3) BlockPos pos) {
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            PlantData data = Tweaks.PLANT_DATA.get((Block) (Object) this);
            if (data != null) {
                float temp = world.getBiome(pos).value().getTemperature();
                if ((temp > data.max && temp <= data.aMax) || (temp < data.min && temp >= data.aMin)) {
                    if (Utilities.RANDOM.nextInt(2) == 0) {
                        ci.cancel();
                    }
                } else if ((temp > data.aMax) || (temp < data.aMin)) {
                    ci.cancel();
                }
            }
        }
    }
}
