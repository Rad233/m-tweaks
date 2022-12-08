package me.melontini.tweaks.registries;

import me.melontini.crackerutil.content.RegistryUtil;
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

import static me.melontini.tweaks.Tweaks.MODID;

public class ItemRegistry {

    public static RoseOfTheValley ROSE_OF_THE_VALLEY = (RoseOfTheValley) RegistryUtil.createItem(Tweaks.CONFIG.unknown, RoseOfTheValley.class, new Identifier(MODID, "rose_of_the_valley"), BlockRegistry.ROSE_OF_THE_VALLEY, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static SpawnerMinecartItem SPAWNER_MINECART = (SpawnerMinecartItem) RegistryUtil.createItem(SpawnerMinecartItem.class, new Identifier(MODID, "spawner_minecart"), AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static AnvilMinecartItem ANVIL_MINECART = (AnvilMinecartItem) RegistryUtil.createItem(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn, AnvilMinecartItem.class, new Identifier(MODID, "anvil_minecart"), new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static NoteBlockMinecartItem NOTE_BLOCK_MINECART = (NoteBlockMinecartItem) RegistryUtil.createItem(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn, NoteBlockMinecartItem.class, new Identifier(MODID, "note_block_minecart"), new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static JukeBoxMinecartItem JUKEBOX_MINECART = (JukeBoxMinecartItem) RegistryUtil.createItem(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn, JukeBoxMinecartItem.class, new Identifier(MODID, "jukebox_minecart"), new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static BlockItem INCUBATOR = (BlockItem) RegistryUtil.createItem(Tweaks.CONFIG.incubatorSettings.enableIncubator, BlockItem.class, new Identifier(MODID, "incubator"), BlockRegistry.INCUBATOR_BLOCK, new FabricItemSettings().rarity(Rarity.RARE).group(ItemGroup.DECORATIONS));
    public static Item INFINITE_TOTEM = RegistryUtil.createItem(Tweaks.CONFIG.totemSettings.enableInfiniteTotem, Item.class, new Identifier(MODID, "infinite_totem"), new FabricItemSettings().maxCount(1).group(ItemGroup.COMBAT).rarity(Rarity.EPIC));

    public static void register() {
        for (BoatEntity.Type value : BoatEntity.Type.values()) {
            if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
                Registry.register(Registry.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_furnace"), new FurnaceBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
                Registry.register(Registry.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_jukebox"), new JukeboxBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
                Registry.register(Registry.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_tnt"), new TNTBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
                Registry.register(Registry.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_hopper"), new HopperBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        LogUtil.info("ItemRegistry init complete!");
    }
}
