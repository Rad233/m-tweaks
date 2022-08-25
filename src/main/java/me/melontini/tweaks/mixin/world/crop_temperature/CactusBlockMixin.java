package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import me.melontini.tweaks.util.data.PlantData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.SugarCaneBlock.AGE;

@MixinRelatedConfigOption("temperatureBasedCropGrowthSpeed")
@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin extends Block {
    public CactusBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/BlockState.get (Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;", shift = At.Shift.AFTER), method = "randomTick", cancellable = true)
    public void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        int age = state.get(AGE);
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            PlantData data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId((CactusBlock) (Object) this));
            if (data != null) {
                float temp = world.getBiome(pos).value().getTemperature();
                if (temp >= data.min && temp <= data.max) {
                    if (age == 15) {
                        world.setBlockState(pos.up(), this.getDefaultState());
                        world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                    } else {
                        world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                    }
                } else if ((temp > data.max && temp <= data.aMax) || (temp < data.min && temp >= data.aMin)) {
                    if (world.getRandom().nextInt(3) == 0) {
                        if (age == 15) {
                            world.setBlockState(pos.up(), this.getDefaultState());
                            world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                        } else {
                            world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                        }
                    }
                }
                ci.cancel();
            }
        }
    }
}
