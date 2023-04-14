package me.melontini.tweaks.mixin.world.epic_fire;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@MixinRelatedConfigOption("quickFire")
@Mixin(FireBlock.class)
public abstract class AbstractFireBlockMixin extends AbstractFireBlock {
    public AbstractFireBlockMixin(Settings settings, float damage) {
        super(settings, damage);
    }

    @Shadow
    protected abstract int getSpreadChance(BlockState state);

    @Shadow
    protected abstract BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age);

    @Shadow
    protected abstract void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge);

    @ModifyVariable(method = "trySpreadingFire", at = @At(value = "LOAD"), index = 3, argsOnly = true)
    public int mTweaks$spreadFire0(int value) {
        return !Tweaks.CONFIG.quickFire ? value : (int) (value * 0.8);
    }

    @ModifyExpressionValue(method = "trySpreadingFire", at = @At(value = "CONSTANT", args = "intValue=10"))
    public int mTweaks$spreadFire01(int value) {
        return!Tweaks.CONFIG.quickFire ? value : (int) Math.ceil(value / 3d);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/block/FireBlock.trySpreadingFire (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/util/math/random/Random;I)V", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "scheduledTick")
    public void mTweaks$trySpreadBlocks(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci, int i, boolean bl2, int k) {
        if (Tweaks.CONFIG.quickFire) {
            for (int x = -3; x < 3; x++) {
                for (int y = -3; y < 3; y++) {
                    for (int z = -3; z < 3; z++) {
                        this.trySpreadingFire(world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), 300 + k, random, i);
                    }
                }
            }
        }
    }
}
