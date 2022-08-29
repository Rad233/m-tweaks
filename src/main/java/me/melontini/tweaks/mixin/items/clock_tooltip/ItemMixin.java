package me.melontini.tweaks.mixin.items.clock_tooltip;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.TextUtil;
import me.melontini.tweaks.util.annotations.MixinRelatedConfigOption;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@MixinRelatedConfigOption("clockTooltip")
@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "appendTooltip")
    public void mTweaks$tooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (Tweaks.CONFIG.clockTooltip) if (world != null) if (world.isClient) {
            if (stack.getItem() == Items.CLOCK) {
                //totally not stolen from here https://bukkit.org/threads/how-can-i-convert-minecraft-long-time-to-real-hours-and-minutes.122912/
                int i = (int) Math.floor((world.getTimeOfDay() / 1000 + 8) % 24);
                int j = (int) Math.floor(60 * (world.getTimeOfDay() % 1000) / 1000);
                tooltip.add(TextUtil.applyFormatting(TextUtil.createTranslatable("tooltip.m-tweaks.clock", String.format("%02d:%02d", i, j)), Formatting.GRAY));
            }
        }
    }
}
