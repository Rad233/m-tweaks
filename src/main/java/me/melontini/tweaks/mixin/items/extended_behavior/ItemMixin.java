package me.melontini.tweaks.mixin.items.extended_behavior;

import me.melontini.tweaks.duck.ThrowableBehaviorDuck;
import me.melontini.tweaks.util.ItemBehavior;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
@MixinRelatedConfigOption("throwableItems")
public class ItemMixin implements ThrowableBehaviorDuck {
    private ItemBehavior mTweaks$throwableBehavior;
    @Override
    public boolean mTweaks$hasBehavior() {
        return mTweaks$throwableBehavior != null;
    }

    @Override
    public void mTweaks$setBehavior(ItemBehavior itemBehavior) {
        this.mTweaks$throwableBehavior = itemBehavior;
    }

    @Override
    public ItemBehavior mTweaks$getBehavior() {
        return this.mTweaks$throwableBehavior;
    }
}
