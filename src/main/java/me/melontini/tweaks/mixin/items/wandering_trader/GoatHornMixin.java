package me.melontini.tweaks.mixin.items.wandering_trader;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import me.melontini.tweaks.util.WorldUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
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
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "use", cancellable = true)
    private void wanderingGoatHorn(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack, Optional<RegistryEntry<Instrument>> optional, Instrument instrument) {
        NbtCompound nbtCompound = itemStack.getNbt();
        LogUtil.info("mixin loaded!");
        if (Tweaks.CONFIG.tradingGoatHorn) if (!world.isClient()) if (nbtCompound != null) {
            if (nbtCompound.getString("instrument") != null) {
                LogUtil.info("played horn {}", nbtCompound.getString("instrument"));
                if (Objects.equals(nbtCompound.getString("instrument"), "minecraft:sing_goat_horn")) {
                    Tweaks.PLANT_DATA.forEach((identifier, plantData) -> LogUtil.info("identifier: {}, min: {}, max: {}", identifier, plantData.min, plantData.max));
                    LogUtil.info("Trader spawn cooldown: {}", WorldUtil.getTraderManager((ServerWorld) world).cooldown);

                    var server = world.getServer();
                    if (server != null) {
                        if (world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING))
                            WorldUtil.getTraderManager((ServerWorld) world).trySpawn((ServerWorld) world, server.getSaveProperties().getMainWorldProperties(), user);
                    }
                }
                if (FabricLoader.getInstance().isDevelopmentEnvironment()) if (Objects.equals(nbtCompound.getString("instrument"), "minecraft:yearn_goat_horn")) {
                    int i = user.getInventory().size();
                    for (int j = 0; j < i; j++) {
                        user.getInventory().getStack(j).damage(500, Random.create(), (ServerPlayerEntity) user);
                    }
                    cir.setReturnValue(TypedActionResult.pass(itemStack));
                }
            }
        }
    }
}
