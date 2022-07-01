package me.melontini.tweaks.mixin.crop_temperature;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable {
    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/CropBlock.getAvailableMoisture (Lnet/minecraft/block/Block;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F", shift = At.Shift.AFTER), method = "randomTick", cancellable = true)
    public void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        CropBlock cropBlock = (CropBlock) (Object) this;
        if (config.cropsGrowSlowerInCold) {
            //No accessors?
            int age = cropBlock.getAge(state);
            float f = CropBlock.getAvailableMoisture(this, world, pos);
            float temp = world.getBiome(pos).value().getTemperature();
            if (temp > 0 && temp < 0.6) {
                //LogUtil.info("cold " + temp);
                if (random.nextInt((int) (25.0F / (f * (temp + 0.2))) + 1) == 0) {
                    world.setBlockState(pos, cropBlock.withAge(age + 1), Block.NOTIFY_LISTENERS);
                }
            } else if (temp >= 0.6) {
                //LogUtil.info("normal " + temp);
                if (random.nextInt((int) ((25.0F / f) + 1)) == 0) {
                    world.setBlockState(pos, cropBlock.withAge(age + 1), Block.NOTIFY_LISTENERS);
                }
            }
            //sheeeeesh
            ci.cancel();
        }
    }
}
