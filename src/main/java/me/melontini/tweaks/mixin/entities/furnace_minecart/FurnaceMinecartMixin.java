package me.melontini.tweaks.mixin.entities.furnace_minecart;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
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

@MixinRelatedConfigOption("betterFurnaceMinecart")
@Mixin(FurnaceMinecartEntity.class)
public class FurnaceMinecartMixin {
    @Shadow
    public int fuel;

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    public void mTweaks$interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();

        FurnaceMinecartEntity furnaceMinecart = (FurnaceMinecartEntity) (Object) this;
        if (Tweaks.CONFIG.betterFurnaceMinecart) {
            if (FuelRegistryImpl.INSTANCE.get(item) != null) {
                int itemFuel = FuelRegistryImpl.INSTANCE.get(item);
                if ((this.fuel + (itemFuel * 2.25)) <= Tweaks.CONFIG.maxFurnaceMinecartFuel) {
                    if (!player.getAbilities().creativeMode) {
                        if (stack.getItem().getRecipeRemainder() != null)
                            player.getInventory().offerOrDrop(stack.getItem().getRecipeRemainder().getDefaultStack());
                        stack.decrement(1);
                    }

                    this.fuel += (itemFuel * 2.25);
                }
            }

            if (this.fuel > 0) {
                furnaceMinecart.pushX = furnaceMinecart.getX() - player.getX();
                furnaceMinecart.pushZ = furnaceMinecart.getZ() - player.getZ();
            }

            cir.setReturnValue(ActionResult.success(furnaceMinecart.world.isClient));
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putShort(Ljava/lang/String;S)V"), method = "writeCustomDataToNbt")
    private void mTweaks$fuelIntToNbt(NbtCompound nbt, String key, /* short */ short value) {
        if (Tweaks.CONFIG.betterFurnaceMinecart) nbt.putInt(key, this.fuel);
        else nbt.putShort(key, value);
    }

    @Inject(at = @At(value = "TAIL"), method = "readCustomDataFromNbt")
    public void mTweaks$fuelIntFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (Tweaks.CONFIG.betterFurnaceMinecart) this.fuel = nbt.getInt("Fuel");
    }
}
