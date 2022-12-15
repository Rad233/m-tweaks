package me.melontini.tweaks.util;

import me.melontini.crackerutil.util.MakeSure;
import me.melontini.tweaks.Tweaks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemStackUtil {

    public static void spawn(@NotNull BlockPos pos, ItemStack stack, World world) {
        MakeSure.notNulls(pos, stack, world);
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    public static void spawn(@NotNull Vec3d pos, ItemStack stack, World world) {
        MakeSure.notNulls(pos, stack, world);
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    public static void spawnWithRVelocity(@NotNull BlockPos pos, ItemStack stack, World world, double range) {
        MakeSure.notNulls(pos, stack, world);
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack,
                Tweaks.RANDOM.nextDouble(range + range) - range,
                0,
                Tweaks.RANDOM.nextDouble(range + range) - range);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }

    public static void spawnWithRVelocity(@NotNull Vec3d pos, ItemStack stack, World world, double range) {
        MakeSure.notNulls(pos, stack, world);
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack,
                Tweaks.RANDOM.nextDouble(range + range) - range,
                0,
                Tweaks.RANDOM.nextDouble(range + range) - range);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }
}
