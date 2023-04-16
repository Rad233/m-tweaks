package me.melontini.tweaks.registries;

import me.melontini.crackerutil.client.util.DrawUtil;
import me.melontini.crackerutil.content.ContentBuilder;
import me.melontini.crackerutil.util.Utilities;
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
import me.melontini.tweaks.util.TweaksLog;
import me.melontini.tweaks.util.TweaksTexts;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static me.melontini.crackerutil.content.RegistryUtil.asItem;
import static me.melontini.crackerutil.content.RegistryUtil.createItem;
import static me.melontini.tweaks.Tweaks.MODID;

public class ItemRegistry {
    public static RoseOfTheValley ROSE_OF_THE_VALLEY = asItem(BlockRegistry.ROSE_OF_THE_VALLEY);
    public static SpawnerMinecartItem SPAWNER_MINECART = ContentBuilder.ItemBuilder.create(SpawnerMinecartItem.class, new Identifier(MODID, "spawner_minecart"), AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings())
            .maxCount(1).itemGroup(ItemGroup.TRANSPORTATION).build();
    public static AnvilMinecartItem ANVIL_MINECART = ContentBuilder.ItemBuilder.create(AnvilMinecartItem.class, new Identifier(MODID, "anvil_minecart"), new FabricItemSettings())
            .maxCount(1).itemGroup(ItemGroup.TRANSPORTATION).loadCondition(Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn).build();
    public static NoteBlockMinecartItem NOTE_BLOCK_MINECART = ContentBuilder.ItemBuilder.create(NoteBlockMinecartItem.class, new Identifier(MODID, "note_block_minecart"), new FabricItemSettings())
            .maxCount(1).itemGroup(ItemGroup.TRANSPORTATION).loadCondition(Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn).build();
    public static JukeBoxMinecartItem JUKEBOX_MINECART = ContentBuilder.ItemBuilder.create(JukeBoxMinecartItem.class, new Identifier(MODID, "jukebox_minecart"), new FabricItemSettings())
            .maxCount(1).itemGroup(ItemGroup.TRANSPORTATION).loadCondition(Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn).build();
    public static Item INFINITE_TOTEM = ContentBuilder.ItemBuilder.create(Item.class, new Identifier(MODID, "infinite_totem"), new FabricItemSettings())
            .maxCount(1).rarity(Rarity.EPIC).itemGroup(ItemGroup.COMBAT).loadCondition(Tweaks.CONFIG.totemSettings.enableInfiniteTotem).build();
    public static BlockItem INCUBATOR = asItem(BlockRegistry.INCUBATOR_BLOCK);
    private static final ItemStack ITEM_GROUP_ICON = Utilities.supply(() -> {
        if (Tweaks.CONFIG.unknown) {
            return new ItemStack(ROSE_OF_THE_VALLEY);
        }
        if (Tweaks.CONFIG.incubatorSettings.enableIncubator) {
            return new ItemStack(INCUBATOR);
        }
        return new ItemStack(SPAWNER_MINECART);
    });
    public static ItemGroup GROUP = ContentBuilder.ItemGroupBuilder.create(new Identifier(MODID, "group"))
            .entries(itemStacks -> {
                List<ItemStack> misc = new ArrayList<>();
                if (Tweaks.CONFIG.incubatorSettings.enableIncubator) misc.add(ItemRegistry.INCUBATOR.getDefaultStack());
                if (Tweaks.CONFIG.totemSettings.enableInfiniteTotem) misc.add(ItemRegistry.INFINITE_TOTEM.getDefaultStack());
                Utilities.appendStacks(itemStacks, misc);

                List<ItemStack> carts = new ArrayList<>();
                if (Tweaks.CONFIG.newMinecarts.isAnvilMinecartOn) carts.add(ItemRegistry.ANVIL_MINECART.getDefaultStack());
                if (Tweaks.CONFIG.newMinecarts.isJukeboxMinecartOn)
                    carts.add(ItemRegistry.JUKEBOX_MINECART.getDefaultStack());
                if (Tweaks.CONFIG.newMinecarts.isNoteBlockMinecartOn)
                    carts.add(ItemRegistry.NOTE_BLOCK_MINECART.getDefaultStack());
                carts.add(ItemRegistry.SPAWNER_MINECART.getDefaultStack());
                Utilities.appendStacks(itemStacks, carts);

                List<ItemStack> boats = new ArrayList<>();
                for (BoatEntity.Type value : BoatEntity.Type.values()) {
                    if (Tweaks.CONFIG.newBoats.isFurnaceBoatOn)
                        boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_furnace")).getDefaultStack());
                    if (Tweaks.CONFIG.newBoats.isJukeboxBoatOn)
                        boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_jukebox")).getDefaultStack());
                    if (Tweaks.CONFIG.newBoats.isTNTBoatOn)
                        boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_tnt")).getDefaultStack());
                    if (Tweaks.CONFIG.newBoats.isHopperBoatOn)
                        boats.add(Registry.ITEM.get(new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_hopper")).getDefaultStack());
                }
                Utilities.appendStacks(itemStacks, boats, false);
            }).icon(() -> ITEM_GROUP_ICON).animatedIcon(() -> (matrixStack, itemX, itemY, selected, isTopRow) -> {
                MinecraftClient client = MinecraftClient.getInstance();

                float angle = Util.getMeasuringTimeMs() * 0.09f;
                matrixStack.push();
                matrixStack.translate(itemX, itemY, 100.0F + client.getItemRenderer().zOffset);
                matrixStack.translate(8.0, 8.0, 0.0);
                matrixStack.scale(1.0F, -1.0F, 1.0F);
                matrixStack.scale(16.0F, 16.0F, 16.0F);
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle));
                BakedModel model = client.getItemRenderer().getModel(ITEM_GROUP_ICON, null, null, 0);
                DrawUtil.renderGuiItemModelCustomMatrixNoTransform(matrixStack, ITEM_GROUP_ICON, model);
                matrixStack.pop();
            }).displayName(TweaksTexts.ITEM_GROUP_NAME).build();

    public static void register() {
        for (BoatEntity.Type value : BoatEntity.Type.values()) {
            createItem(Tweaks.CONFIG.newBoats.isFurnaceBoatOn, FurnaceBoatItem.class, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_furnace"), value, new FabricItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION));
            createItem(Tweaks.CONFIG.newBoats.isJukeboxBoatOn, JukeboxBoatItem.class, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_jukebox"), value, new FabricItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION));
            createItem(Tweaks.CONFIG.newBoats.isTNTBoatOn, TNTBoatItem.class, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_tnt"), value, new FabricItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION));
            createItem(Tweaks.CONFIG.newBoats.isHopperBoatOn, HopperBoatItem.class, new Identifier(MODID, value.getName().replace(":", "_") + "_boat_with_hopper"), value, new FabricItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION));
        }
        TweaksLog.info("ItemRegistry init complete!");
    }
}
