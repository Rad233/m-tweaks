package me.melontini.tweaks.registries;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.items.RoseOfTheValley;
import me.melontini.tweaks.items.boats.FurnaceBoatItem;
import me.melontini.tweaks.items.boats.HopperBoatItem;
import me.melontini.tweaks.items.boats.JukeboxBoatItem;
import me.melontini.tweaks.items.boats.TNTBoatItem;
import me.melontini.tweaks.items.minecarts.AnvilMinecartItem;
import me.melontini.tweaks.items.minecarts.JukeBoxMinecartItem;
import me.melontini.tweaks.items.minecarts.NoteBlockMinecartItem;
import me.melontini.tweaks.items.minecarts.SpawnerMinecartItem;
import me.melontini.tweaks.util.LogUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.melontini.tweaks.Tweaks.MODID;

@SuppressWarnings("rawtypes")
public class ItemRegistry {


    public static RoseOfTheValley ROSE_OF_THE_VALLEY = (RoseOfTheValley) createItem(Tweaks.CONFIG.unknown, RoseOfTheValley.class, "rose_of_the_valley", BlockRegistry.ROSE_OF_THE_VALLEY, new FabricItemSettings().rarity(Rarity.UNCOMMON));

    public static SpawnerMinecartItem SPAWNER_MINECART = (SpawnerMinecartItem) createItem(SpawnerMinecartItem.class, "spawner_minecart", ItemGroups.REDSTONE, AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().maxCount(1));
    public static AnvilMinecartItem ANVIL_MINECART = (AnvilMinecartItem) createItem(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn, AnvilMinecartItem.class, "anvil_minecart", ItemGroups.REDSTONE, new FabricItemSettings().maxCount(1));
    public static NoteBlockMinecartItem NOTE_BLOCK_MINECART = (NoteBlockMinecartItem) createItem(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn, NoteBlockMinecartItem.class, "note_block_minecart", ItemGroups.REDSTONE, new FabricItemSettings().maxCount(1));
    public static JukeBoxMinecartItem JUKEBOX_MINECART = (JukeBoxMinecartItem) createItem(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn, JukeBoxMinecartItem.class, "jukebox_minecart", ItemGroups.REDSTONE, new FabricItemSettings().maxCount(1));
    public static BlockItem INCUBATOR = (BlockItem) createItem(Tweaks.CONFIG.incubatorSettings.enableIncubator, BlockItem.class, "incubator", ItemGroups.FUNCTIONAL, BlockRegistry.INCUBATOR_BLOCK, new FabricItemSettings().rarity(Rarity.RARE));
    public static Item INFINITE_TOTEM = createItem(Tweaks.CONFIG.totemSettings.enableInfiniteTotem, Item.class, "infinite_totem", ItemGroups.COMBAT, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC));

    public static void register() {
        for (BoatEntity.Type value : BoatEntity.Type.values()) {
            createItem(Tweaks.CONFIG.newBoats.isFurnaceBoatOn, FurnaceBoatItem.class, value.getName().replace(":", "_") + "_boat_with_furnace", ItemGroups.REDSTONE, value, new FabricItemSettings().maxCount(1));
            createItem(Tweaks.CONFIG.newBoats.isJukeboxBoatOn, JukeboxBoatItem.class, value.getName().replace(":", "_") + "_boat_with_jukebox", ItemGroups.REDSTONE, value, new FabricItemSettings().maxCount(1));
            createItem(Tweaks.CONFIG.newBoats.isTNTBoatOn, TNTBoatItem.class, value.getName().replace(":", "_") + "_boat_with_tnt", ItemGroups.REDSTONE, value, new FabricItemSettings().maxCount(1));
            createItem(Tweaks.CONFIG.newBoats.isHopperBoatOn, HopperBoatItem.class, value.getName().replace(":", "_") + "_boat_with_hopper", ItemGroups.REDSTONE, value, new FabricItemSettings().maxCount(1));
        }
        LogUtil.info("ItemRegistry init complete!");
    }

    public static @Nullable Item createItem(Class<?> itemClass, String identifier, Object... params) {
        return createItem(true, itemClass, identifier, Optional.empty(), params);
    }

    public static @Nullable Item createItem(Class<?> itemClass, String identifier, ItemGroup group, Object... params) {
        return createItem(true, itemClass, identifier, Optional.of(group), params);
    }

    public static @Nullable Item createItem(boolean shouldRegister, Class<?> itemClass, String identifier, Object... params) {
        return createItem(shouldRegister, itemClass, identifier, Optional.empty(), params);
    }

    public static @Nullable Item createItem(boolean shouldRegister, Class<?> itemClass, String identifier, ItemGroup group, Object... params) {
        return createItem(shouldRegister, itemClass, identifier, Optional.of(group), params);
    }

    public static @Nullable Item createItem(boolean shouldRegister, Class<?> itemClass, String identifier, Optional<ItemGroup> group, Object... params) {
        if (shouldRegister) {
            List<Class<?>> list = new ArrayList<>();
            for (Object o : params) {
                list.add(o.getClass());
            }
            Item item;
            try {
                item = (Item)ConstructorUtils.getMatchingAccessibleConstructor(itemClass, list.toArray(Class[]::new)).newInstance(params);
            } catch (Exception e) {
                throw new RuntimeException(String.format("[" + MODID + "] couldn't create item. identifier: %s", identifier), e);
            }

            Registry.register(Registries.ITEM, new Identifier(MODID, identifier), item);

            group.ifPresent(itemGroup -> ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item)));
            return item;
        } else {
            return null;
        }
    }
}
