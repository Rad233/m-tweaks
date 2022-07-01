package me.melontini.tweaks.util;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerUtil {
    public static List<PlayerEntity> findPlayersInRange(World world, BlockPos pos, int range) {
        return world.getPlayers(TargetPredicate.createNonAttackable().setBaseMaxDistance(range), null,
                new Box(pos).expand(range));
    }

    public static List<PlayerEntity> findNonCreativePlayersInRange(World world, BlockPos pos, int range) {
        List<PlayerEntity> players = world.getPlayers(TargetPredicate.createNonAttackable().setBaseMaxDistance(range), null,
                new Box(pos).expand(range));
        List<PlayerEntity> nonCreative = new ArrayList<>();
        for (PlayerEntity player : players) {
            if (!player.isSpectator() && !player.isCreative()) {
                nonCreative.add(player);
            }
        }
        return nonCreative;
    }

    public static PlayerEntity pickRandomPlayerFromList(List<PlayerEntity> playerEntities) {
        int random = new Random().nextInt(playerEntities.size());
        return playerEntities.get(random);
    }
}
