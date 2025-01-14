package me.melontini.tweaks.items;

import me.melontini.tweaks.util.TweaksTexts;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoseOfTheValley extends BlockItem {

    public RoseOfTheValley(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(TweaksTexts.ROSE_OF_THE_VALLEY_TOOLTIP);
    }
}
