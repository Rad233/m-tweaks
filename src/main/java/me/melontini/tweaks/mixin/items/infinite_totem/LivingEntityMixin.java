package me.melontini.tweaks.mixin.items.infinite_totem;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static me.melontini.tweaks.Tweaks.MODID;

@MixinRelatedConfigOption("totemSettings.enableInfiniteTotem")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @ModifyVariable(method = "tryUseTotem", at = @At(value = "LOAD"), index = 2, ordinal = 0)
    private ItemStack mTweaks$infiniteFallback(ItemStack itemStack) {
        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem) {
            for (Hand hand : Hand.values()) {
                ItemStack itemStack1 = this.getStackInHand(hand);
                if (itemStack1.isOf(ItemRegistry.INFINITE_TOTEM)) {
                    itemStack = itemStack1.copy();
                    break;
                }
            }
        }
        return itemStack;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "tryUseTotem", cancellable = true)
    private void mTweaks$useInfiniteTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir, ItemStack itemStack) {
        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem) {
            if (itemStack.isOf(ItemRegistry.INFINITE_TOTEM)) {
                if (!world.isClient()) {
                    PlayerLookup.tracking(this).forEach(entity -> ServerPlayNetworking.send(entity, new Identifier(MODID, "infinite_totem_use"), PacketByteBufs.create().writeUuid(this.getUuid())));
                }
                cir.setReturnValue(true);
            }
        }
    }
}
