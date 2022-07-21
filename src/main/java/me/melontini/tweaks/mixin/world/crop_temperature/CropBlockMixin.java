package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable {
    @Shadow
    protected abstract int getAge(BlockState state);

    @Shadow public abstract BlockState withAge(int age);

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    /*
    var data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId(block));
    if (temp >= data.min && temp <= data.max) {

    } else if (temp > data.max && temp <= data.aMax) {

    } else if (temp < data.min && temp >= data.aMin) {

    }
     */

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/CropBlock.getAvailableMoisture (Lnet/minecraft/block/Block;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F", shift = At.Shift.AFTER), method = "randomTick", cancellable = true)
    public void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        CropBlock cropBlock = (CropBlock) (Object) this;
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            //No accessors?
            int age = this.getAge(state);
            float f = CropBlock.getAvailableMoisture(this, world, pos);
            float temp = world.getBiome(pos).value().getTemperature();
            var data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId(cropBlock));
            if (data != null) {
                var rand = temp < 1.0D ? (25.0F / (f * (temp + 0.4))) + 1 : (25.0F / (f / (temp - 0.4))) + 1;
                if (temp >= data.min && temp <= data.max) {
                    LogUtil.info("normal {} ", temp);
                    if (random.nextInt((int) ((25.0F / f) + 1)) == 0) {
                        world.setBlockState(pos, this.withAge(age + 1), Block.NOTIFY_LISTENERS);
                    }
                } else if (temp > data.max && temp <= data.aMax) {
                    LogUtil.info("hot {} ", temp);
                    if (random.nextInt((int) rand) == 0) {
                        world.setBlockState(pos, this.withAge(age + 1), Block.NOTIFY_LISTENERS);
                    }
                } else if (temp < data.min && temp >= data.aMin) {
                    LogUtil.info("cold {} ", temp);
                    if (random.nextInt((int) rand) == 0) {
                        world.setBlockState(pos, this.withAge(age + 1), Block.NOTIFY_LISTENERS);
                    }
                }
                ci.cancel();
            }
        }
    }
}
