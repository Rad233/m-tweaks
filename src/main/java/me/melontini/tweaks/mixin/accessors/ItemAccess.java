package me.melontini.tweaks.mixin.accessors;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccess {
    @Invoker("isIn")
    boolean isIn(ItemGroup group);
}
