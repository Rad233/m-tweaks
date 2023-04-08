package me.melontini.tweaks.mixin.items.mending_fix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@MixinRelatedConfigOption("balancedMending")
@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    public AnvilScreenHandlerMixin(@org.jetbrains.annotations.Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @ModifyExpressionValue(method = "updateResult", at = @At(value = "CONSTANT", args = "intValue=40"))
    private int mTweaks$setRepairLimit(int constant) {
        if (Tweaks.CONFIG.balancedMending)
            if (!this.getSlot(1).getStack().isOf(Items.ENCHANTED_BOOK))
                if (EnchantmentHelper.get(this.getSlot(0).getStack()).containsKey(Enchantments.MENDING)) {
                    return Integer.MAX_VALUE;
                }
        return constant;
    }
}
