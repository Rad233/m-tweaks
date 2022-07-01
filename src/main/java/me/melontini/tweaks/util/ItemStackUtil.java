package me.melontini.tweaks.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemStackUtil {
    public static void spawnItem(@NotNull BlockPos pos, ItemStack stack, World world) {
        ItemEntity itemEntity = new ItemEntity(
                world,
                pos.getX(),
                pos.getY() + 0.2,
                pos.getZ(),
                stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    public static void spawnItem(@NotNull Vec3d pos, ItemStack stack, World world) {
        ItemEntity itemEntity = new ItemEntity(
                world,
                pos.getX(),
                pos.getY() + 0.2,
                pos.getZ(),
                stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    public static void spawnItemWithRandVelocity(@NotNull BlockPos pos, ItemStack stack, World world) {
        ItemEntity itemEntity = new ItemEntity(
                world,
                pos.getX(),
                pos.getY() + 0.2,
                pos.getZ(),
                stack,
                (Math.random() - 0.5) * 0.5,
                0,
                (Math.random() - 0.5) * 0.5);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    public static void spawnItemWithRandVelocity(@NotNull Vec3d pos, ItemStack stack, World world) {
        ItemEntity itemEntity = new ItemEntity(
                world,
                pos.getX(),
                pos.getY() + 0.2,
                pos.getZ(),
                stack,
                (Math.random() - 0.5) * 0.5,
                0,
                (Math.random() - 0.5) * 0.5);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }
}
