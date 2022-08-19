package me.melontini.tweaks.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CustomTraderManager extends PersistentState {

    private final Random random = Random.create();
    public int cooldown;

    //I'm so good at making bad decisions
    public CustomTraderManager() {
    }

    public void readNbt(@NotNull NbtCompound nbt) {
        this.cooldown = nbt.getInt("mt-trd-cooldown");
    }

    @Override
    public NbtCompound writeNbt(@NotNull NbtCompound nbt) {
        nbt.putInt("mt-trd-cooldown", this.cooldown);
        return nbt;
    }

    public void tick() {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
    }

    public void trySpawn(ServerWorld world, ServerWorldProperties properties, PlayerEntity player) {
        if (cooldown == 0) if (player != null) {
            BlockPos blockPos = player.getBlockPos();

            PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
            Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
                    registryEntry -> registryEntry.matchesKey(PointOfInterestTypes.MEETING), pos -> true, blockPos, 48, PointOfInterestStorage.OccupationStatus.ANY
            );
            BlockPos blockPos2 = optional.orElse(blockPos);
            BlockPos blockPos3 = getNearbySpawnPos(world, blockPos2, 48);
            if (blockPos3 != null && doesNotSuffocateAt(world, blockPos3)) {
                if (world.getBiome(blockPos3).isIn(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                    return;
                }

                WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(world, null, null, null, blockPos3, SpawnReason.EVENT, false, false);
                if (wanderingTraderEntity != null) {
                    cooldown = 48000;
                    for (int j = 0; j < 2; ++j) {
                        spawnLlama(world, wanderingTraderEntity);
                    }

                    properties.setWanderingTraderId(wanderingTraderEntity.getUuid());
                    wanderingTraderEntity.setDespawnDelay(48000);
                    wanderingTraderEntity.setWanderTarget(blockPos2);
                    wanderingTraderEntity.setPositionTarget(blockPos2, 16);
                }
            }
        }
    }

    private void spawnLlama(ServerWorld world, @NotNull WanderingTraderEntity wanderingTrader) {
        BlockPos blockPos = this.getNearbySpawnPos(world, wanderingTrader.getBlockPos(), 4);
        if (blockPos != null) {
            TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(world, null, null, null, blockPos, SpawnReason.EVENT, false, false);
            if (traderLlamaEntity != null) {
                traderLlamaEntity.attachLeash(wanderingTrader, true);
            }
        }
    }

    @Nullable
    private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
        BlockPos blockPos = null;

        for (int i = 0; i < 10; ++i) {
            int x = pos.getX() + this.random.nextInt(range * 2) - range;
            int z = pos.getZ() + this.random.nextInt(range * 2) - range;
            int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
            BlockPos blockPos2 = new BlockPos(x, y, z);
            if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos2, EntityType.WANDERING_TRADER)) {
                blockPos = blockPos2;
                break;
            }
        }

        return blockPos;
    }

    private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
            if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
