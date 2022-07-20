package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.AbstractPlantPartBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractPlantStemBlock.class)
public abstract class AbstractPlantStemBlockMixin extends AbstractPlantPartBlock {
    @Shadow @Final private double growthChance;

    protected AbstractPlantStemBlockMixin(Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater) {
        super(settings, growthDirection, outlineShape, tickWater);
    }

    @Shadow protected abstract boolean chooseStemState(BlockState state);

    @Shadow @Final public static IntProperty AGE;

    @Shadow protected abstract BlockState age(BlockState state, Random random);

    @Inject(at = @At("HEAD"), method = "randomTick")
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            float temp = world.getBiome(pos).value().getTemperature();
            var data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId((AbstractPlantStemBlock) (Object) this));
            if (data != null) {
                if (temp >= data.min && temp <= data.max) {
                    if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance) {
                        BlockPos blockPos = pos.offset(this.growthDirection);
                        if (this.chooseStemState(world.getBlockState(blockPos))) {
                            world.setBlockState(blockPos, this.age(state, world.random));
                        }
                    }
                } else if (temp > data.max && temp <= data.aMax) {
                    /*if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance) {
                        BlockPos blockPos = pos.offset(this.growthDirection);
                        if (this.chooseStemState(world.getBlockState(blockPos))) {
                            world.setBlockState(blockPos, this.age(state, world.random));
                        }
                    }*/
                } else if (temp < data.min && temp >= data.aMin) {

                }
            } else {
                if (state.get(AGE) < 25 && random.nextDouble() < this.growthChance) {
                    BlockPos blockPos = pos.offset(this.growthDirection);
                    if (this.chooseStemState(world.getBlockState(blockPos))) {
                        world.setBlockState(blockPos, this.age(state, world.random));
                    }
                }
            }
        }
    }
}
