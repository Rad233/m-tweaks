package me.melontini.tweaks.registries;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.items.boats.*;
import me.melontini.tweaks.items.minecarts.AnvilMinecartItem;
import me.melontini.tweaks.items.minecarts.JukeBoxMinecartItem;
import me.melontini.tweaks.items.minecarts.NoteBlockMinecartItem;
import me.melontini.tweaks.items.minecarts.SpawnerMinecartItem;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static me.melontini.tweaks.Tweaks.MODID;

public class ItemRegistry {
    public static  Item SPAWNER_MINECART;
    public static  Item ANVIL_MINECART;
    public static  Item NOTE_BLOCK_MINECART;
    public static Item JUKEBOX_MINECART;
    public static void register() {
        SPAWNER_MINECART = new SpawnerMinecartItem(AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));

        TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        Registry.register(Registry.ITEM, new Identifier(MODID, "spawner_minecart"), SPAWNER_MINECART);

        if (config.newMinecarts.isAnvilMinecartOn) {
            ANVIL_MINECART = new AnvilMinecartItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
            Registry.register(Registry.ITEM, new Identifier(MODID, "anvil_minecart"), ANVIL_MINECART);
        }
        if (config.newMinecarts.isNoteBlockMinecartOn) {
            NOTE_BLOCK_MINECART = new NoteBlockMinecartItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
            Registry.register(Registry.ITEM, new Identifier(MODID, "note_block_minecart"), NOTE_BLOCK_MINECART);
        }
        if (config.newMinecarts.isJukeboxMinecartOn) {
            JUKEBOX_MINECART = new JukeBoxMinecartItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
            Registry.register(Registry.ITEM, new Identifier(MODID, "jukebox_minecart"), JUKEBOX_MINECART);
        }

        if (config.newBoats.isFurnaceBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_furnace"), new FurnaceBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        if (config.newBoats.isJukeboxBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_jukebox"), new JukeboxBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        if (config.newBoats.isTNTBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_tnt"), new TNTBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        if (config.newBoats.isHopperBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_hopper"), new HopperBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        if (config.newBoats.isChestBoatOn) for (BoatEntity.Type value : BoatEntity.Type.values()) {
            Registry.register(Registry.ITEM, new Identifier(MODID, value.getName() + "_boat_with_chest"), new ChestBoatItem(value, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1)));
        }
        LogUtil.info("ItemRegistry init complete!");
    }
}
