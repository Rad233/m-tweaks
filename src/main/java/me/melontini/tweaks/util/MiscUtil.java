package me.melontini.tweaks.util;

import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Random;

public class MiscUtil {
    public static <T> T pickRandomEntryFromList(List<T> list) {
        int random = new Random().nextInt(list.size());
        return list.get(random);
    }

    public static CustomTraderManager getTraderManager(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(nbtCompound -> {
            var manager = new CustomTraderManager();
            manager.fromTag(nbtCompound);
            return manager;
        }, CustomTraderManager::new, "mt_trader_statemanager");
    }
}
