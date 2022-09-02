package me.melontini.tweaks.util.concurrent;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import static net.minecraft.entity.ItemEntity.STACK;

public record ItemDiscoveryRunnable(ItemEntity itemEntity, Set<ItemEntity> takenEntities) implements Callable<Optional<ItemEntity>> {

    @Override
    public Optional<ItemEntity> call() {
        try {
            List<ItemEntity> list = itemEntity.world.getEntitiesByClass(ItemEntity.class, new Box(itemEntity.getPos().x + 0.5, itemEntity.getPos().y + 0.5, itemEntity.getPos().z + 0.5, itemEntity.getPos().x - 0.5, itemEntity.getPos().y - 0.5, itemEntity.getPos().z - 0.5),
                    itemEntity1 -> itemEntity1.getDataTracker().get(STACK).isOf(Items.NETHER_STAR) && !takenEntities.contains(itemEntity1));
            return list.stream().findAny();
        } catch (ConcurrentModificationException e) {
            return Optional.empty(); //I have 0 IQ
        }
    }
}
