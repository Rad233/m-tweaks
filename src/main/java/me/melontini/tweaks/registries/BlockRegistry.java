package me.melontini.tweaks.registries;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.blocks.IncubatorBlock;
import me.melontini.tweaks.blocks.entities.IncubatorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static me.melontini.tweaks.Tweaks.MODID;

public class BlockRegistry {
    public static FlowerBlock ROSE_OF_THE_VALLEY;

    public static IncubatorBlock INCUBATOR_BLOCK;
    public static BlockEntityType<IncubatorBlockEntity> INCUBATOR_BLOCK_ENTITY;
    public static void register() {
        if (Tweaks.CONFIG.unknown) {
            ROSE_OF_THE_VALLEY = new FlowerBlock(StatusEffects.REGENERATION, 12, AbstractBlock.Settings.copy(Blocks.LILY_OF_THE_VALLEY));
            Registry.register(Registry.BLOCK, new Identifier(MODID, "rose_of_the_valley"), ROSE_OF_THE_VALLEY);
        }

        if (Tweaks.CONFIG.incubatorSettings.enableIncubator) {
            INCUBATOR_BLOCK = new IncubatorBlock(FabricBlockSettings.of(Material.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
            Registry.register(Registry.BLOCK, new Identifier(MODID, "incubator"), INCUBATOR_BLOCK);
            INCUBATOR_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(IncubatorBlockEntity::new).addBlock(INCUBATOR_BLOCK).build();
            Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "incubator"), INCUBATOR_BLOCK_ENTITY);
        }
    }
}
