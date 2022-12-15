package me.melontini.tweaks.registries;

import me.melontini.crackerutil.client.util.DrawUtil;
import me.melontini.crackerutil.content.RegistryUtil;
import me.melontini.crackerutil.util.MathStuff;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.List;

import static me.melontini.tweaks.Tweaks.MODID;

public class ItemRegistry {

    private static final DefaultedList<ItemStack> EMPTY_LIST = DefaultedList.ofSize(9, ItemStack.EMPTY);
    public static RoseOfTheValley ROSE_OF_THE_VALLEY = (RoseOfTheValley) RegistryUtil.createItem(Tweaks.CONFIG.unknown, RoseOfTheValley.class, new Identifier(MODID, "rose_of_the_valley"), BlockRegistry.ROSE_OF_THE_VALLEY, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static SpawnerMinecartItem SPAWNER_MINECART = (SpawnerMinecartItem) RegistryUtil.createItem(SpawnerMinecartItem.class, new Identifier(MODID, "spawner_minecart"), ItemGroups.REDSTONE, AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().maxCount(1));
    public static AnvilMinecartItem ANVIL_MINECART = (AnvilMinecartItem) RegistryUtil.createItem(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn, AnvilMinecartItem.class, new Identifier(MODID, "anvil_minecart"), ItemGroups.REDSTONE, new FabricItemSettings().maxCount(1));
    public static NoteBlockMinecartItem NOTE_BLOCK_MINECART = (NoteBlockMinecartItem) RegistryUtil.createItem(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn, NoteBlockMinecartItem.class, new Identifier(MODID, "note_block_minecart"), ItemGroups.REDSTONE, new FabricItemSettings().maxCount(1));
    public static JukeBoxMinecartItem JUKEBOX_MINECART = (JukeBoxMinecartItem) RegistryUtil.createItem(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn, JukeBoxMinecartItem.class, new Identifier(MODID, "jukebox_minecart"), ItemGroups.REDSTONE, new FabricItemSettings().maxCount(1));
    public static BlockItem INCUBATOR = (BlockItem) RegistryUtil.createItem(Tweaks.CONFIG.incubatorSettings.enableIncubator, BlockItem.class, new Identifier(MODID, "incubator"), ItemGroups.REDSTONE, BlockRegistry.INCUBATOR_BLOCK, new FabricItemSettings().rarity(Rarity.RARE));
    public static Item INFINITE_TOTEM = RegistryUtil.createItem(Tweaks.CONFIG.totemSettings.enableInfiniteTotem, Item.class, new Identifier(MODID, "infinite_totem"), ItemGroups.COMBAT, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC));
    private static final ItemStack INCUBATOR_STACK = new ItemStack(INCUBATOR);

    public static ItemGroup GROUP = FabricItemGroup.builder(new Identifier(MODID, "group")).entries((enabledFeatures, entries, operatorEnabled) -> {
        ((ItemGroup.EntriesImpl)entries).parentTabStacks = new ArrayList<>();

        List<ItemStack> misc = new ArrayList<>();
        if (Tweaks.CONFIG.incubatorSettings.enableIncubator) misc.add(ItemRegistry.INCUBATOR.getDefaultStack());
        if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem)
            misc.add(ItemRegistry.INFINITE_TOTEM.getDefaultStack());
        appendStacks(entries, misc);

        List<ItemStack> carts = new ArrayList<>();
        if (Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn) carts.add(ItemRegistry.ANVIL_MINECART.getDefaultStack());
        if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn)
            carts.add(ItemRegistry.JUKEBOX_MINECART.getDefaultStack());
        if (Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn)
            carts.add(ItemRegistry.NOTE_BLOCK_MINECART.getDefaultStack());
        carts.add(ItemRegistry.SPAWNER_MINECART.getDefaultStack());
        appendStacks(entries, carts);

        List<ItemStack> boats = new ArrayList<>();
        for (BoatEntity.Type value : BoatEntity.Type.values()) {
            if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
                boats.add(Registries.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_furnace")).getDefaultStack());
            if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
                boats.add(Registries.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_jukebox")).getDefaultStack());
            if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
                boats.add(Registries.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_tnt")).getDefaultStack());
            if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
                boats.add(Registries.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_hopper")).getDefaultStack());
        }
        appendStacks(entries, boats);
    }).icon(() -> INCUBATOR_STACK).displayName(Text.translatable("itemGroup.m_tweaks.items")).build();

    private static void appendStacks(ItemGroup.Entries entries, List<ItemStack> list) {
        if (list.isEmpty()) return; //we shouldn't add line breaks if there are no items.

        int rows = MathStuff.fastCeil(list.size() / 9d);
        ((ItemGroup.EntriesImpl)entries).parentTabStacks.addAll(list);
        int left = (rows * 9) - list.size();
        for (int i = 0; i < left; i++) {
            ((ItemGroup.EntriesImpl)entries).parentTabStacks.add(ItemStack.EMPTY); //fill the gaps
        }
        ((ItemGroup.EntriesImpl)entries).parentTabStacks.addAll(EMPTY_LIST); //line break
    }

    public static void register() {
        for (BoatEntity.Type value : BoatEntity.Type.values()) {
            if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
                Registry.register(Registries.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_furnace"), new FurnaceBoatItem(value, new FabricItemSettings().maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
                Registry.register(Registries.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_jukebox"), new JukeboxBoatItem(value, new FabricItemSettings().maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
                Registry.register(Registries.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_tnt"), new TNTBoatItem(value, new FabricItemSettings().maxCount(1)));
            if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
                Registry.register(Registries.ITEM, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_hopper"), new HopperBoatItem(value, new FabricItemSettings().maxCount(1)));
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            GROUP.setIconAnimation((stack, l, m) -> {
                MinecraftClient client = MinecraftClient.getInstance();

                float angle = Util.getMeasuringTimeMs() * 0.09f;
                stack.push();
                stack.translate(l, m, 100.0F + client.getItemRenderer().zOffset);
                stack.translate(8.0, 8.0, 0.0);
                stack.scale(1.0F, -1.0F, 1.0F);
                stack.scale(16.0F, 16.0F, 16.0F);
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
                BakedModel model = client.getItemRenderer().getModel(INCUBATOR_STACK, null, null, 0);
                DrawUtil.renderGuiItemModelCustomMatrixNoTransform(stack, INCUBATOR_STACK, model);
                stack.pop();
            });
        }
        LogUtil.info("ItemRegistry init complete!");
    }
}
