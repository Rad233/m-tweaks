package me.melontini.tweaks.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BeeNestUtil {
    //TODO generalize this, since I could probably iterate over blockentities' nbt
    public static void trySpawnFallingBeeNest(World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull BeehiveBlockEntity beehiveBlockEntity) {
        FallingBlockEntity fallingBlock = new FallingBlockEntity(
                world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                state.contains(Properties.WATERLOGGED) ? state.with(Properties.WATERLOGGED, Boolean.FALSE) : state);

        NbtCompound beeData = new NbtCompound();
        NbtCompound tileData = new NbtCompound();
        beeData.put("Bees", beehiveBlockEntity.getBees());
        beeData.putBoolean("MT-FromFallenBlock", true);
        tileData.put("TileEntityData", beeData);
        tileData.put("BlockState", NbtHelper.fromBlockState(state));
        //Thanks AccessWidener!
        fallingBlock.readCustomDataFromNbt(tileData);

        world.setBlockState(pos, state.getFluidState().getBlockState());
        world.spawnEntity(fallingBlock);
    }
}
