package me.melontini.tweaks.registries;

import me.melontini.crackerutil.content.ContentBuilder;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import static me.melontini.crackerutil.content.RegistryUtil.asItem;
import static me.melontini.tweaks.Tweaks.MODID;

public class ItemRegistry {

    private static final DefaultedList<ItemStack> EMPTY_LIST = DefaultedList.ofSize(9, ItemStack.EMPTY);
    public static RoseOfTheValley ROSE_OF_THE_VALLEY = asItem(BlockRegistry.ROSE_OF_THE_VALLEY);
    public static SpawnerMinecartItem SPAWNER_MINECART = ContentBuilder.ItemBuilder.create(SpawnerMinecartItem.class, new Identifier(MODID, "spawner_minecart"), AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().maxCount(1))
            .itemGroup(ItemGroup.TRANSPORTATION).build();
    public static AnvilMinecartItem ANVIL_MINECART = ContentBuilder.ItemBuilder.create(AnvilMinecartItem.class, new Identifier(MODID, "anvil_minecart"), new FabricItemSettings().maxCount(1))
            .itemGroup(ItemGroup.TRANSPORTATION).loadCondition(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn).build();
    public static NoteBlockMinecartItem NOTE_BLOCK_MINECART = ContentBuilder.ItemBuilder.create(NoteBlockMinecartItem.class, new Identifier(MODID, "note_block_minecart"), new FabricItemSettings().maxCount(1))
            .itemGroup(ItemGroup.TRANSPORTATION).loadCondition(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn).build();
    public static JukeBoxMinecartItem JUKEBOX_MINECART = ContentBuilder.ItemBuilder.create(JukeBoxMinecartItem.class, new Identifier(MODID, "jukebox_minecart"), new FabricItemSettings().maxCount(1))
            .itemGroup(ItemGroup.TRANSPORTATION).loadCondition(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn).build();
    public static BlockItem INCUBATOR = asItem(BlockRegistry.INCUBATOR_BLOCK);
    public static Item INFINITE_TOTEM = ContentBuilder.ItemBuilder.create(Item.class, new Identifier(MODID, "infinite_totem"), new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC))
            .itemGroup(ItemGroup.COMBAT).loadCondition(Tweaks.CONFIG.totemSettings.enableInfiniteTotem).build();

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
        LogUtil.devInfo("ItemRegistry init complete!");
    }
}
