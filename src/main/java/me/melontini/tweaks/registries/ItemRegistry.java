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
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static me.melontini.tweaks.Tweaks.MODID;

public class ItemRegistry {

    public static RoseOfTheValley ROSE_OF_THE_VALLEY;
    public static  Item SPAWNER_MINECART;
    public static  Item ANVIL_MINECART;
    public static  Item NOTE_BLOCK_MINECART;
    public static Item JUKEBOX_MINECART;
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

        if (Tweaks.CONFIG.unknown) {
            ROSE_OF_THE_VALLEY = new RoseOfTheValley(BlockRegistry.ROSE_OF_THE_VALLEY, new FabricItemSettings().rarity(Rarity.UNCOMMON));
            Registry.register(Registry.ITEM, new Identifier(MODID, "rose_of_the_valley"), ROSE_OF_THE_VALLEY);
        }
        LogUtil.info("ItemRegistry init complete!");
    }
}
