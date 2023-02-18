package me.melontini.tweaks.duck;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public interface LinkableMinecartsDuck {

    AbstractMinecartEntity mTweaks$getFollowing();

    void mTweaks$setFollowing(AbstractMinecartEntity following);

    AbstractMinecartEntity mTweaks$getFollower();

    void mTweaks$setFollower(AbstractMinecartEntity follower);
}
