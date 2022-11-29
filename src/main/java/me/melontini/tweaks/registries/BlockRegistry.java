package me.melontini.tweaks.registries;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.blocks.IncubatorBlock;
import me.melontini.tweaks.blocks.entities.IncubatorBlockEntity;
import me.melontini.tweaks.util.LogUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static me.melontini.tweaks.Tweaks.MODID;

@SuppressWarnings("rawtypes")
public class BlockRegistry {
    public static FlowerBlock ROSE_OF_THE_VALLEY = (FlowerBlock) createBlock(Tweaks.CONFIG.unknown, FlowerBlock.class, "rose_of_the_valley", StatusEffects.REGENERATION, 12, AbstractBlock.Settings.copy(Blocks.LILY_OF_THE_VALLEY));
    public static IncubatorBlock INCUBATOR_BLOCK = (IncubatorBlock) createBlock(Tweaks.CONFIG.incubatorSettings.enableIncubator, IncubatorBlock.class, "incubator", FabricBlockSettings.of(Material.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));

    public static void register() {
        LogUtil.info("BlockRegistry init complete!");
    }    public static BlockEntityType<IncubatorBlockEntity> INCUBATOR_BLOCK_ENTITY = createBlockEntity(INCUBATOR_BLOCK != null, "incubator", FabricBlockEntityTypeBuilder.create(IncubatorBlockEntity::new).addBlock(INCUBATOR_BLOCK));

    public static Block createBlock(Class<?> blockClass, String id, Object... params) {
        return createBlock(true, blockClass, id, params);
    }

    public static Block createBlock(boolean shouldRegister, Class<?> blockClass, String id, Object... params) {
        if (shouldRegister) {
            List<Class> list = new ArrayList<>();
            for (Object o : params) {
                list.add(o.getClass());
            }
            Block block;
            try {
                block = (Block) ConstructorUtils.getMatchingAccessibleConstructor(blockClass, list.toArray(Class[]::new)).newInstance(params);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(String.format("[" + MODID + "] couldn't create block. identifier: %s", id), e);
            }

            Registry.register(Registries.BLOCK, new Identifier(MODID, id), block);
            return block;
        }
        return null;
    }

    public static BlockEntityType createBlockEntity(String id, FabricBlockEntityTypeBuilder builder) {
        return createBlockEntity(true, id, builder);
    }

    public static BlockEntityType createBlockEntity(boolean shouldRegister, String id, FabricBlockEntityTypeBuilder builder) {
        if (shouldRegister) {
            BlockEntityType type = builder.build();
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MODID, id), type);
            return type;
        }
        return null;
    }




}
