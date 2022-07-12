package me.melontini.tweaks.mixin.crop_temperature;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
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
        CropBlock cropBlock = (CropBlock) (Object) this;
        if (Tweaks.CONFIG.cropsGrowSlowerInCold) {
            //No accessors?
            int age = cropBlock.getAge(state);
            float f = CropBlock.getAvailableMoisture(this, world, pos);
            float temp = world.getBiome(pos).getTemperature();
            if (temp > 0 && temp < 0.6) {
                //LogUtil.info("cold " + temp);
                if (random.nextInt((int) (25.0F / (f * (temp + 0.2))) + 1) == 0) {
                    world.setBlockState(pos, cropBlock.withAge(age + 1), 2);
                }
            } else if (temp >= 0.6) {
                //LogUtil.info("normal " + temp);
                if (random.nextInt((int) ((25.0F / f) + 1)) == 0) {
                    world.setBlockState(pos, cropBlock.withAge(age + 1), 2);
                }
            }
            //sheeeeesh
            ci.cancel();
        }
    }
}
