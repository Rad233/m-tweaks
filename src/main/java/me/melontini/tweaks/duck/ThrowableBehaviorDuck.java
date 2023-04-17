package me.melontini.tweaks.duck;

import me.melontini.tweaks.util.ItemBehavior;

public interface ThrowableBehaviorDuck {
    boolean mTweaks$hasBehavior();

    void mTweaks$setBehavior(ItemBehavior itemBehavior);
    ItemBehavior mTweaks$getBehavior();
}
