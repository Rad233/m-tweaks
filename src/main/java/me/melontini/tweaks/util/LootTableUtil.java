package me.melontini.tweaks.util;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class LootTableUtil {
    public static List<ItemStack> prepareLoot(World world, Identifier lootId) {
        return (
                (ServerWorld) world)
                .getServer()
                .getLootManager()
                .getTable(lootId)
                .generateLoot((
                        new LootContext.Builder((ServerWorld) world))
                        .random(world.random)
                        .build(LootContextTypes.EMPTY)
                );
    }
}
