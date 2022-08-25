package me.melontini.tweaks.mixin.items.mending_fix;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
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
    @Unique
    private ItemStack mTweaks$fakeStack = ItemStack.EMPTY;

    public AnvilScreenHandlerMixin(@org.jetbrains.annotations.Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @ModifyArg(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 4), index = 1)
    private ItemStack mTweaks$setFakeStack(ItemStack stack) {
        if (Tweaks.CONFIG.balancedMending) if (!this.getSlot(1).getStack().isOf(Items.ENCHANTED_BOOK))
            if (EnchantmentHelper.get(mTweaks$fakeStack).containsKey(Enchantments.MENDING)) {
                return mTweaks$fakeStack;
            }
        return stack;
    }

    @Inject(at = @At(value = "FIELD", target = "net/minecraft/item/ItemStack.EMPTY : Lnet/minecraft/item/ItemStack;", opcode = Opcodes.GETSTATIC, ordinal = 5, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "updateResult")
    private void mTweaks$updateResult(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3, Map map) {
        if (Tweaks.CONFIG.balancedMending) mTweaks$fakeStack = itemStack2.copy();
    }
}
