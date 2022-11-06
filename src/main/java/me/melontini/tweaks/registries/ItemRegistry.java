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
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static me.melontini.tweaks.Tweaks.MODID;

@SuppressWarnings("rawtypes")
public class ItemRegistry {

    public static RoseOfTheValley ROSE_OF_THE_VALLEY = (RoseOfTheValley) createItem(Tweaks.CONFIG.unknown, RoseOfTheValley.class, "rose_of_the_valley", BlockRegistry.ROSE_OF_THE_VALLEY, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static SpawnerMinecartItem SPAWNER_MINECART = (SpawnerMinecartItem) createItem(SpawnerMinecartItem.class, "spawner_minecart", AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static AnvilMinecartItem ANVIL_MINECART = (AnvilMinecartItem) createItem(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn, AnvilMinecartItem.class, "anvil_minecart", new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static NoteBlockMinecartItem NOTE_BLOCK_MINECART = (NoteBlockMinecartItem) createItem(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn, NoteBlockMinecartItem.class, "note_block_minecart", new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static JukeBoxMinecartItem JUKEBOX_MINECART = (JukeBoxMinecartItem) createItem(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn, JukeBoxMinecartItem.class, "jukebox_minecart", new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static BlockItem INCUBATOR = (BlockItem) createItem(Tweaks.CONFIG.incubatorSettings.enableIncubator, BlockItem.class, "incubator", BlockRegistry.INCUBATOR_BLOCK, new FabricItemSettings().rarity(Rarity.RARE).group(ItemGroup.DECORATIONS));
    public static Item INFINITE_TOTEM = createItem(Tweaks.CONFIG.totemSettings.enableInfiniteTotem, Item.class, "infinite_totem", new FabricItemSettings().maxCount(1).group(ItemGroup.COMBAT).rarity(Rarity.EPIC));

    public static void register() {
        for (BoatEntity.Type value : BoatEntity.Type.values()) {
            if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn) Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_furnace"), new FurnaceBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn) Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_jukebox"), new JukeboxBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isTNTBoatOn) Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_tnt"), new TNTBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isHopperBoatOn) Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_hopper"), new HopperBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        LogUtil.info("ItemRegistry init complete!");
    }

    public static Item createItem(Class<?> itemClass, String identifier, Object... params) {
        return createItem(true, itemClass, identifier, params);
    }

    public static @Nullable Item createItem(boolean shouldRegister, Class<?> itemClass, String identifier, Object... params) {
        if (shouldRegister) {
            List<Class> list = new ArrayList<>();
            for (Object o : params) {
                list.add(o.getClass());
            }
            Item item;
            try {
                item = (Item) ConstructorUtils.getMatchingAccessibleConstructor(itemClass, list.toArray(Class[]::new)).newInstance(params);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(String.format("[" + MODID + "] couldn't create item. identifier: %s", identifier), e);
            }

            Registry.register(Registry.ITEM, new Identifier(MODID, identifier), item);
            return item;
        } else {
            return null;
        }
    }
}
