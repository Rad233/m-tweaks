package me.melontini.tweaks.mixin.items.extended_behavior;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.duck.ThrowableBehaviorDuck;
import me.melontini.tweaks.entity.FlyingItemEntity;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
@MixinRelatedConfigOption("throwableItems")
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract void decrement(int amount);

    @ModifyReturnValue(at = @At("RETURN"), method = "use")
    private TypedActionResult<ItemStack> mTweaks$throwableBehaviour(TypedActionResult<ItemStack> original, World world, PlayerEntity user, Hand hand) {
        if (Tweaks.CONFIG.throwableItems && original.getResult() == ActionResult.PASS && (((ThrowableBehaviorDuck)getItem()).mTweaks$hasBehavior() || Tweaks.ITEM_BEHAVIOR_DATA.containsKey(getItem()))) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
            if (!world.isClient) {
                var entity = new FlyingItemEntity((ItemStack) (Object) this, user, world);
                entity.setPos(user.getX(), user.getEyeY() - 0.1F, user.getZ());
                entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
                world.spawnEntity(entity);
            }

            user.incrementStat(Stats.USED.getOrCreateStat(getItem()));
            if (!user.getAbilities().creativeMode) {
                this.decrement(1);
            }

            user.getItemCooldownManager().set(getItem(), 50);

            return TypedActionResult.success((ItemStack) (Object) this);
        }
        return original;
    }
}
