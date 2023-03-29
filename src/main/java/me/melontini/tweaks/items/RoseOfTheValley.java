package me.melontini.tweaks.items;

import me.melontini.crackerutil.client.particles.Particle;
import me.melontini.crackerutil.client.util.ScreenParticleHelper;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.util.TweaksTexts;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class RoseOfTheValley extends BlockItem {

    public RoseOfTheValley(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(TweaksTexts.ROSE_OF_THE_VALLEY_TOOLTIP);
    }

    public static void handleClick(ItemStack stack, ItemStack otherStack, PlayerEntity player) {
        player.getInventory().offerOrDrop(new ItemStack(ItemRegistry.ROSE_OF_THE_VALLEY));
        stack.decrement(1);
        otherStack.decrement(1);
        if (player.world.isClient) {
            var client = MinecraftClient.getInstance();
            int x = (int) (client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth());
            int y = (int) (client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight());
            for (int i = 0; i < 20; i++) {
                ScreenParticleHelper.addParticle(new Particle(x, y, MathHelper.nextDouble(player.world.random, -1, 1), MathHelper.nextDouble(player.world.random, -1, -0.5), Color.RED.darker().getRGB()) {
                    @Override
                    protected void tickLogic() {
                        x += Math.sin((player.age) / 8.0 * velX);
                        y += velY * 0.99;
                    }
                });
            }
        }
    }
}
