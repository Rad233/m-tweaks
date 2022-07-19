package me.melontini.tweaks.mixin.blocks.better_fletching_table;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.LogUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends RangedWeaponItem {
    public BowItemMixin(Settings settings) {
        super(settings);
    }

    //sin
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER), method = "onStoppedUsing", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void m_tweaks$setVelocity(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        NbtCompound stackNbt = stack.getNbt();
        if (Tweaks.CONFIG.usefulFletching) if (stackNbt != null) if (stackNbt.contains("MT-Tightened")) if (stackNbt.getInt("MT-Tightened") > 0) {
            int a = stackNbt.getInt("MT-Tightened");
            persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, 0.2F);
            LogUtil.info(a);
            stackNbt.putInt("MT-Tightened", a - 1);
            stack.setNbt(stackNbt);
        }

    }
}
