package me.melontini.tweaks.mixin.items.mending_fix;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinRelatedConfigOption("balancedMending")
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(at = @At("RETURN"), method = "getRepairCost", cancellable = true)
    private void mTweaks$getRepairCost(CallbackInfoReturnable<Integer> cir) {
        if (Tweaks.CONFIG.balancedMending && cir.getReturnValue() >= 52 && EnchantmentHelper.get((ItemStack) (Object) this).containsKey(Enchantments.MENDING)) {
            cir.setReturnValue(52);
        }
        LogUtil.devInfo(cir.getReturnValue());
    }
}
