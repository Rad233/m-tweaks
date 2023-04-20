package me.melontini.tweaks.mixin.items.mending_fix;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
@MixinRelatedConfigOption("balancedMending")
public abstract class ItemStackMixin {
    @ModifyReturnValue(method = "getRepairCost", at = @At("RETURN"))
    private int mTweaks$getRepairCost(int original) {
        if (Tweaks.CONFIG.balancedMending && original >= 52 && EnchantmentHelper.get((ItemStack) (Object) this).containsKey(Enchantments.MENDING)) {
            return 52;
        }
        return original;
    }
}
