package me.melontini.tweaks.mixin.world.crop_temperature;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushMixin {
    @Shadow @Final public static IntProperty AGE;

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    private void mTweaks$randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (Tweaks.CONFIG.temperatureBasedCropGrowthSpeed) {
            float temp = world.getBiome(pos).value().getTemperature();
            var data = Tweaks.PLANT_DATA.get(Registry.BLOCK.getId((SweetBerryBushBlock) (Object) this));
            int i = state.get(AGE);
            if (data != null) {
                if (temp >= data.min && temp <= data.max) {
                    if (i < 3 && random.nextInt(5) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                        BlockState blockState = state.with(AGE, i + 1);
                        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos);
                    }
                } else if ((temp > data.max && temp <= data.aMax) && (temp < data.min && temp >= data.aMin)) {
                    if (i < 3 && random.nextInt(10) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                        BlockState blockState = state.with(AGE, i + 1);
                        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos);
                    }
                }
                ci.cancel();
            }
        }
    }
}
