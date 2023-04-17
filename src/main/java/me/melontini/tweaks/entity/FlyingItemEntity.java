package me.melontini.tweaks.entity;

import me.melontini.tweaks.duck.ThrowableBehaviorDuck;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class FlyingItemEntity extends ThrownItemEntity {


    public FlyingItemEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        this.setItem(ItemStack.EMPTY);
    }

    public FlyingItemEntity(ItemStack stack, double d, double e, double f, World world) {
        super(EntityTypeRegistry.FLYING_ITEM, d, e, f, world);
        this.setItem(stack);
    }

    public FlyingItemEntity(ItemStack stack, LivingEntity livingEntity, World world) {
        super(EntityTypeRegistry.FLYING_ITEM, livingEntity, world);
        this.setItem(stack);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        ThrowableBehaviorDuck duck = (ThrowableBehaviorDuck) getItem().getItem();
        if (duck.mTweaks$hasBehavior()) {
            duck.mTweaks$getBehavior().onCollision(getItem(), this, this.world, getOwner(), hitResult);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return getItem().getItem();
    }
}
