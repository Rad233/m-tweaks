package me.melontini.tweaks.mixin.crop_temperature;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static net.minecraft.block.SugarCaneBlock.AGE;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin extends Block {
    public CactusBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/BlockState.get (Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;", shift = At.Shift.AFTER), method = "randomTick", cancellable = true)
    public void mTweaks$tick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        int age = state.get(AGE);
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (config.cropsGrowSlowerInCold) {
            float temp = world.getBiome(pos).value().getTemperature();
            if (temp > 0 && temp < 0.6) {
                if (world.getRandom().nextInt((int) ( 25 / (12.5 * (temp + 0.2)))) == 0) {
                    LogUtil.info("cold");
                    if (age == 15) {
                        world.setBlockState(pos.up(), this.getDefaultState());
                        world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                    } else {
                        world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                    }
                }

            } else if (temp >= 0.6) {
                LogUtil.info("normal");
                if (age == 15) {
                    world.setBlockState(pos.up(), this.getDefaultState());
                    world.setBlockState(pos, state.with(AGE, 0), Block.NO_REDRAW);
                } else {
                    world.setBlockState(pos, state.with(AGE, age + 1), Block.NO_REDRAW);
                }
            }
            ci.cancel();
        }
    }
}
