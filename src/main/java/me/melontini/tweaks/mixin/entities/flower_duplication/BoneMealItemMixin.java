package me.melontini.tweaks.mixin.entities.flower_duplication;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    @Inject(at = @At("HEAD"), method = "useOnFertilizable", cancellable = true)
    private static void mTweaks$useOnFertilizable(ItemStack stack, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        var blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof TallFlowerBlock && Tweaks.CONFIG.beeTallFlowerDuplication) {
            cir.setReturnValue(false);
        }
    }
}
