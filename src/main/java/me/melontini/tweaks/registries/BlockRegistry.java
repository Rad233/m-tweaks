package me.melontini.tweaks.registries;

import me.melontini.crackerutil.content.RegistryUtil;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.blocks.IncubatorBlock;
import me.melontini.tweaks.blocks.entities.IncubatorBlockEntity;
import me.melontini.tweaks.util.LogUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static me.melontini.tweaks.Tweaks.MODID;

public class BlockRegistry {
    public static FlowerBlock ROSE_OF_THE_VALLEY = (FlowerBlock) RegistryUtil.createBlock(Tweaks.CONFIG.unknown, FlowerBlock.class, new Identifier(MODID, "rose_of_the_valley"), StatusEffects.REGENERATION, 12, AbstractBlock.Settings.copy(Blocks.LILY_OF_THE_VALLEY));
    public static IncubatorBlock INCUBATOR_BLOCK = (IncubatorBlock) RegistryUtil.createBlock(Tweaks.CONFIG.incubatorSettings.enableIncubator, IncubatorBlock.class, new Identifier(MODID, "incubator"), FabricBlockSettings.of(Material.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static BlockEntityType<IncubatorBlockEntity> INCUBATOR_BLOCK_ENTITY = RegistryUtil.createBlockEntity(INCUBATOR_BLOCK != null, new Identifier(MODID, "incubator"), BlockEntityType.Builder.create(IncubatorBlockEntity::new, INCUBATOR_BLOCK));

    public static void register() {
        LogUtil.info("BlockRegistry init complete!");
    }


}
