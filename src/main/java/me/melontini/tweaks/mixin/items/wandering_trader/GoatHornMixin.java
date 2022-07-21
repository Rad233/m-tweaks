package me.melontini.tweaks.mixin.items.wandering_trader;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;
import java.util.Optional;

@Mixin(GoatHornItem.class)
public class GoatHornMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, method = "use")
    private void mTweaks$wanderingGoatHorn(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack, Optional<RegistryEntry<Instrument>> optional, Instrument instrument) {
        NbtCompound nbtCompound = itemStack.getNbt();
        if (Tweaks.CONFIG.tradingGoatHorn) if (!world.isClient()) if (nbtCompound != null) {
            if (nbtCompound.getString("instrument") != null) {
                LogUtil.info("played horn {}", nbtCompound.getString("instrument"));
                if (Objects.equals(nbtCompound.getString("instrument"), "minecraft:sing_goat_horn")) {
                    LogUtil.info("Trader spawn cooldown: {}", WorldUtil.getTraderManager((ServerWorld) world).cooldown);

                    var server = world.getServer();
                    if (server != null) {
                        if (world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING))
                            WorldUtil.getTraderManager((ServerWorld) world).trySpawn((ServerWorld) world, server.getSaveProperties().getMainWorldProperties(), user);
                    }
                }
            }
        }
    }
}
