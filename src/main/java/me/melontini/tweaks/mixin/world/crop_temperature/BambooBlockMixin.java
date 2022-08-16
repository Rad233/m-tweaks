package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.data.PlantData;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.BambooBlock.STAGE;

@Mixin(BambooBlock.class)
public abstract class BambooBlockMixin extends Block implements Fertilizable {
    @Shadow
    protected abstract int countBambooBelow(BlockView world, BlockPos pos);

    @Shadow
    protected abstract void updateLeaves(BlockState state, World world, BlockPos pos, Random random, int height);

    public BambooBlockMixin(Settings settings) {
        super(settings);
    }
    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BambooBlock block = (BambooBlock) (Object) this;
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            if (state.get(STAGE) == 0) {
                PlantData data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId(block));
                if (data != null) {
                    float temp = world.getBiome(pos).value().getTemperature();
                    if (temp >= data.min && temp <= data.max) {
                        if (world.getRandom().nextInt(3) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                            int bambooCount = this.countBambooBelow(world, pos) + 1;
                            if (bambooCount < 16) {
                                this.updateLeaves(state, world, pos, random, bambooCount);
                            }
                        }
                    } else if ((temp > data.max && temp <= data.aMax) || (temp < data.min && temp >= data.aMin)) {
                        if (world.getRandom().nextInt(8) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                            int bambooCount = this.countBambooBelow(world, pos) + 1;
                            if (bambooCount < 16) {
                                this.updateLeaves(state, world, pos, random, bambooCount);
                            }
                        }
                    }
                    ci.cancel();
                }
            }
        }
    }
}
