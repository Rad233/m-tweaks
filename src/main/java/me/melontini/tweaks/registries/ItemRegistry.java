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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static me.melontini.tweaks.Tweaks.MODID;

public class ItemRegistry {

    public static RoseOfTheValley ROSE_OF_THE_VALLEY;
    public static SpawnerMinecartItem SPAWNER_MINECART;
    public static AnvilMinecartItem ANVIL_MINECART;
    public static NoteBlockMinecartItem NOTE_BLOCK_MINECART;
    public static JukeBoxMinecartItem JUKEBOX_MINECART;
    public static BlockItem INCUBATOR;

    public static Item INFINITE_TOTEM;

    public static void register() {
        SPAWNER_MINECART = new SpawnerMinecartItem(AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));

        Registry.register(Registry.ITEM, new Identifier(MODID, "spawner_minecart"), SPAWNER_MINECART);

        if (Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn) {
            ANVIL_MINECART = new AnvilMinecartItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
            Registry.register(Registry.ITEM, new Identifier(MODID, "anvil_minecart"), ANVIL_MINECART);
        }
        if (Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn) {
            NOTE_BLOCK_MINECART = new NoteBlockMinecartItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
            Registry.register(Registry.ITEM, new Identifier(MODID, "note_block_minecart"), NOTE_BLOCK_MINECART);
        }
        if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn) {
            JUKEBOX_MINECART = new JukeBoxMinecartItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
            Registry.register(Registry.ITEM, new Identifier(MODID, "jukebox_minecart"), JUKEBOX_MINECART);
        }

        if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_furnace"), new FurnaceBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_jukebox"), new JukeboxBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        if (Tweaks.CONFIG.newBoats.isTNTBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_tnt"), new TNTBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        if (Tweaks.CONFIG.newBoats.isHopperBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_hopper"), new HopperBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }

        if (Tweaks.CONFIG.unknown)
            ROSE_OF_THE_VALLEY = (RoseOfTheValley) createItem(RoseOfTheValley.class,"rose_of_the_valley", new FabricItemSettings().rarity(Rarity.UNCOMMON), BlockRegistry.ROSE_OF_THE_VALLEY);

        if (Tweaks.CONFIG.incubatorSettings.enableIncubator)
            INCUBATOR = (BlockItem) createItem(BlockItem.class,"incubator", new FabricItemSettings().rarity(Rarity.RARE).group(ItemGroup.DECORATIONS), BlockRegistry.INCUBATOR_BLOCK);

        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
            INFINITE_TOTEM = createItem(Item.class, "infinite_totem", new Item.Settings().maxCount(1).group(ItemGroup.COMBAT).rarity(Rarity.EPIC));

        LogUtil.info("ItemRegistry init complete!");
    }

    private static Item createItem(Class<?> itemClass, String identifier, Item.Settings settings, Object... params) {
        List<Class> list = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        for (Object o : params) {
            list.add(o.getClass());
            objects.add(o);
        }
        list.add(settings.getClass());
        objects.add(settings);
        Item item;
        try {
            item = (Item) ConstructorUtils.getMatchingAccessibleConstructor(itemClass, list.toArray(Class[]::new)).newInstance(objects.toArray());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(String.format("[m-tweaks] couldn't create item. identifier: %s", identifier), e);
        }

        Registry.register(Registry.ITEM, new Identifier(MODID, identifier), item);
        return item;
    }
}
