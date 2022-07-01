package me.melontini.tweaks.mixin.furnace_minecart;

import me.melontini.tweaks.config.TweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceMinecartEntity.class)
public class FurnaceMinecartMixin {
    @Shadow
    public int fuel;

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    public void mTweaks$interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();

        FurnaceMinecartEntity furnaceMinecart = (FurnaceMinecartEntity) (Object) this;
        if (config.betterFurnaceMinecart) {
            if (FuelRegistryImpl.INSTANCE.get(item) != null) {
                int itemFuel = FuelRegistryImpl.INSTANCE.get(item);
                if ((furnaceMinecart.fuel + (itemFuel * 2.25)) <= config.maxFurnaceMinecartFuel) {
                    if (!player.getAbilities().creativeMode) {
                        if (stack.getItem().getRecipeRemainder() != null)
                            player.inventory.insertStack(stack.getItem().getRecipeRemainder().getDefaultStack());
                        stack.decrement(1);
                    }

                    furnaceMinecart.fuel += (itemFuel * 2.25);
                }
            }

            if (furnaceMinecart.fuel > 0) {
                furnaceMinecart.pushX = furnaceMinecart.getX() - player.getX();
                furnaceMinecart.pushZ = furnaceMinecart.getZ() - player.getZ();
            }

            cir.setReturnValue(ActionResult.success(furnaceMinecart.world.isClient));
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putShort(Ljava/lang/String;S)V"), method = "writeCustomDataToNbt")
    private void mTweaks$fuelIntToNbt(NbtCompound nbt, String key, /* short */ short value) {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (config.betterFurnaceMinecart) nbt.putInt(key, this.fuel);
        else nbt.putShort(key, value);
    }

    @Inject(at = @At(value = "TAIL"), method = "readCustomDataFromNbt")
    public void mTweaks$fuelIntFromNbt(NbtCompound nbt, CallbackInfo ci) {
        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (config.betterFurnaceMinecart) this.fuel = nbt.getInt("Fuel");
    }
}
