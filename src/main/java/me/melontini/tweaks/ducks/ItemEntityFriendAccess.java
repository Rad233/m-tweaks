package me.melontini.tweaks.ducks;

import net.minecraft.entity.ItemEntity;

public interface ItemEntityFriendAccess {
    void mTweaks$setFriend(ItemEntity friend);

    ItemEntity mTweaks$getFriend();

    boolean mTweaks$hasFriend();
}
