package me.melontini.tweaks.mixin.blocks.better_fletching_table;

import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.util.TextUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "appendTooltip")
    public void mTweaks$tooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (Tweaks.CONFIG.usefulFletching) if (stack.getItem() instanceof BowItem) {
            NbtCompound stackNbt = stack.getNbt();
            if (stackNbt != null) if (stackNbt.contains("MT-Tightened")) if (stackNbt.getInt("MT-Tightened") > 0) {
                tooltip.add(TextUtil.applyFormatting(TextUtil.createTranslatable("tooltip.m-tweaks.bow.tight", stackNbt.getInt("MT-Tightened")), Formatting.GRAY));
            }
        }
    }
}
