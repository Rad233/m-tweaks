package me.melontini.tweaks.mixin.items.mending_fix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@MixinRelatedConfigOption("balancedMending")
@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends HandledScreen<AnvilScreenHandler> {
    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @ModifyExpressionValue(method = "drawForeground", at = @At(value = "CONSTANT", args = "intValue=40"))
    private int mTweaks$setRepairLimit(int constant) {
        if (Tweaks.CONFIG.balancedMending)
            if (!this.handler.getSlot(1).getStack().isOf(Items.ENCHANTED_BOOK))
                if (EnchantmentHelper.get(this.handler.getSlot(0).getStack()).containsKey(Enchantments.MENDING)) {
                    return Integer.MAX_VALUE;
                }
        return constant;
    }
}
