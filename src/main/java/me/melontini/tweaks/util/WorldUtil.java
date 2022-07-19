package me.melontini.tweaks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorldUtil {

    public static CustomTraderManager getTraderManager(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(nbtCompound -> {
            var manager = new CustomTraderManager();
            manager.fromTag(nbtCompound);
            return manager;
        }, CustomTraderManager::new, "mt_trader_statemanager");
    }

    public static List<ItemStack> prepareLoot(World world, Identifier lootId) {
        return ((ServerWorld) world).getServer()
                .getLootManager()
                .getTable(lootId)
                .generateLoot((new LootContext.Builder((ServerWorld) world))
                        .random(world.random)
                        .build(LootContextTypes.EMPTY));
    }

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

        world.setBlockState(pos, state.getFluidState().getBlockState(), Block.NOTIFY_ALL);
        world.spawnEntity(fallingBlock);
    }
}
