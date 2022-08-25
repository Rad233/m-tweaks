package me.melontini.tweaks.mixin.duck_impl;

import me.melontini.tweaks.ducks.ItemEntityFriendAccess;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityFriendMixin implements ItemEntityFriendAccess {
    private ItemEntity mTweaks$friend;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER), method = "tick")
    private void mTweaks$tick(CallbackInfo ci) {
        if (mTweaks$hasFriend() && mTweaks$getFriend().isRemoved()) {
            mTweaks$setFriend(null);
        }
    }

    @Override
    public boolean mTweaks$hasFriend() {
        return mTweaks$friend != null;
    }

    @Override
    public void mTweaks$setFriend(ItemEntity friend) {
        this.mTweaks$friend = friend;
    }

    @Override
    public ItemEntity mTweaks$getFriend() {
        return mTweaks$friend;
    }
}
