package me.melontini.tweaks.mixin.bugfixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.melontini.tweaks.Tweaks;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryChangedCriterion.class)
public class InventoryChangedCriterionMixin {
    @WrapOperation(method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/InventoryChangedCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;III)V"))
    private void mTweaks$triggerConcurrently(InventoryChangedCriterion criterion, ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied, Operation<Void> operation) {
        if (Tweaks.CONFIG.concurrentInvChangedTrigger) {
            Util.getMainWorkerExecutor().submit(() -> operation.call(criterion, player, inventory, stack, full, empty, occupied));
        } else {
            operation.call(criterion, player, inventory, stack, full, empty, occupied);
        }
    }
}
