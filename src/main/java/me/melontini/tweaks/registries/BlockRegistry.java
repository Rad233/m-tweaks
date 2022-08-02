package me.melontini.tweaks.registries;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static me.melontini.tweaks.Tweaks.MODID;

public class BlockRegistry {
    public static FlowerBlock ROSE_OF_THE_VALLEY;
    public static void register() {
        if (Tweaks.CONFIG.unknown) {
            ROSE_OF_THE_VALLEY = new FlowerBlock(StatusEffects.REGENERATION, 12, AbstractBlock.Settings.copy(Blocks.LILY_OF_THE_VALLEY));
            Registry.register(Registry.BLOCK, new Identifier(MODID, "rose_of_the_valley"), ROSE_OF_THE_VALLEY);
        }
    }
}
