package me.melontini.tweaks.registries;

import me.melontini.crackerutil.content.ContentBuilder;
import me.melontini.crackerutil.content.RegistryUtil;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.blocks.IncubatorBlock;
import me.melontini.tweaks.blocks.entities.IncubatorBlockEntity;
import me.melontini.tweaks.items.RoseOfTheValley;
import me.melontini.tweaks.util.TweaksLog;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static me.melontini.tweaks.Tweaks.MODID;

public class BlockRegistry {
    public static FlowerBlock ROSE_OF_THE_VALLEY = ContentBuilder.BlockBuilder
            .create(FlowerBlock.class, new Identifier(MODID, "rose_of_the_valley"), StatusEffects.REGENERATION, 12, AbstractBlock.Settings.copy(Blocks.LILY_OF_THE_VALLEY))
            .loadCondition(Tweaks.CONFIG.unknown)
            .itemBuilder((block, id) -> ContentBuilder.ItemBuilder
                    .create(RoseOfTheValley.class, id, block, new FabricItemSettings().rarity(Rarity.UNCOMMON))).build();
    public static IncubatorBlock INCUBATOR_BLOCK = ContentBuilder.BlockBuilder
            .create(IncubatorBlock.class, new Identifier(MODID, "incubator"), FabricBlockSettings.of(Material.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD))
            .loadCondition(Tweaks.CONFIG.incubatorSettings.enableIncubator)
            .itemBuilder((block, id) -> ContentBuilder.ItemBuilder
                    .create(BlockItem.class, id, block, new FabricItemSettings().rarity(Rarity.RARE))
                    .itemGroup(ItemGroup.REDSTONE))
            .blockEntity((block, id) -> ContentBuilder.BlockEntityBuilder
                    .create(new Identifier(MODID, "incubator"), IncubatorBlockEntity::new, block)).build();

    public static BlockEntityType<IncubatorBlockEntity> INCUBATOR_BLOCK_ENTITY = RegistryUtil.getBlockEntityFromBlock(INCUBATOR_BLOCK);

    public static void register() {
        TweaksLog.info("BlockRegistry init complete!");
    }
}
