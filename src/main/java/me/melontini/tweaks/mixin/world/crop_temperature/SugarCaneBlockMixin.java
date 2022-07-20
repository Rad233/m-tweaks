package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.SugarCaneBlock.AGE;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin extends Block {
    public SugarCaneBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/BlockState.get (Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;", shift = At.Shift.AFTER), method = "randomTick", cancellable = true)
    public void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        int age = state.get(AGE);
        if (Tweaks.CONFIG.cropsGrowSlowerInCold) {
            float temp = world.getBiome(pos).value().getTemperature();
            var data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId((SugarCaneBlock) (Object) this));
            if (data != null) {
                var rand = temp < 1.0D ? (25 / (12.5 * (temp + 0.2))) : (25 / (12.5 / (temp - 0.2)));
                if (temp >= data.min && temp <= data.max) {
                    if (age == 15) {
                        world.setBlockState(pos.up(), this.getDefaultState());
                        world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                    } else {
                        world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                    }
                } else if (temp > data.max && temp <= data.aMax) {
                    if (world.getRandom().nextInt((int) rand) == 0) {
                        if (age == 15) {
                            world.setBlockState(pos.up(), this.getDefaultState());
                            world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                        } else {
                            world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                        }
                    }
                } else if (temp < data.min && temp >= data.aMin) {
                    if (world.getRandom().nextInt((int) rand) == 0) {
                        if (age == 15) {
                            world.setBlockState(pos.up(), this.getDefaultState());
                            world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                        } else {
                            world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                        }
                    }
                }
            } else {
                if (temp > 0 && temp < 0.6) {
                    if (world.getRandom().nextInt((int) (25 / (12.5 * (temp + 0.2)))) == 0) {
                        //LogUtil.info("cold");
                        if (age == 15) {
                            world.setBlockState(pos.up(), this.getDefaultState());
                            world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                        } else {
                            world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                        }
                    }

                } else if (temp >= 0.6) {
                    //LogUtil.info("normal");
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
