package me.melontini.tweaks.mixin.entities.furnace_minecart;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.ItemStackUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@MixinRelatedConfigOption({"betterFurnaceMinecart", "furnaceMinecartTakeFuelWhenLow"})
@Mixin(FurnaceMinecartEntity.class)
public abstract class FurnaceMinecartIntakeMixin extends AbstractMinecartEntity {
    @Shadow
    public int fuel;

    protected FurnaceMinecartIntakeMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void mTweaks$tick(CallbackInfo ci) {
        if (!this.world.isClient() && Tweaks.CONFIG.betterFurnaceMinecart && Tweaks.CONFIG.furnaceMinecartTakeFuelWhenLow && this.fuel < 100) {
            if (world.getTime() % 10 == 0) {
                AbstractMinecartEntity entity = this.world
                        .getEntitiesByClass(AbstractMinecartEntity.class, this.getBoundingBox().expand(1.5, 0, 1.5), minecart -> minecart instanceof Inventory)
                        .stream()
                        .min(Comparator.comparingDouble(value -> value.squaredDistanceTo(this)))
                        .orElse(null);

                if (entity instanceof Inventory inventory) {
                    for (int i = 0; i < inventory.size(); ++i) {
                        ItemStack stack = inventory.getStack(i);
                        if (FuelRegistryImpl.INSTANCE.get(stack.getItem()) != null) {
                            int itemFuel = FuelRegistryImpl.INSTANCE.get(stack.getItem());
                            if ((this.fuel + (itemFuel * 2.25)) <= Tweaks.CONFIG.maxFurnaceMinecartFuel) {
                                if (stack.getItem().getRecipeRemainder() != null)
                                    ItemStackUtil.spawn(entity.getPos(), stack.getItem().getRecipeRemainder().getDefaultStack(), world);
                                stack.decrement(1);

                                this.fuel += (itemFuel * 2.25);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}