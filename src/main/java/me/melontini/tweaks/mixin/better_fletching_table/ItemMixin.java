package me.melontini.tweaks.mixin.better_fletching_table;

import me.melontini.tweaks.Tweaks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
    public void m_tweaks$tooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (stack.getItem() instanceof BowItem) if (Tweaks.CONFIG.usefulFletching) {
            NbtCompound stackNbt = stack.getTag();
            if (stackNbt != null) if (stackNbt.contains("MT-Tightened")) if (stackNbt.getInt("MT-Tightened") > 0) {
                tooltip.add(new TranslatableText("tooltip.m-tweaks.bow.tight", stackNbt.getInt("MT-Tightened")).formatted(Formatting.GRAY, Formatting.ITALIC));
            }
        }
    }
}
