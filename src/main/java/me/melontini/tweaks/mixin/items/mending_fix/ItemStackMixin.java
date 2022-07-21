package me.melontini.tweaks.mixin.items.mending_fix;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(at = @At("RETURN"), method = "getRepairCost", cancellable = true)
    private void getRepairCost(CallbackInfoReturnable<Integer> cir) {
        if (Tweaks.CONFIG.balancedMending && cir.getReturnValue() >= 40 && EnchantmentHelper.get((ItemStack) (Object) this).containsKey(Enchantments.MENDING)) {
            cir.setReturnValue(40);
        }
        LogUtil.info(cir.getReturnValue());
    }
}
