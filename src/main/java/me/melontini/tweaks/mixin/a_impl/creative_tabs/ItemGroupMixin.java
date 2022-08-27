package me.melontini.tweaks.mixin.a_impl.creative_tabs;

import com.google.common.collect.Table;
import me.melontini.tweaks.Tweaks;
import me.melontini.tweaks.ducks.ItemGroupAccess;
import me.melontini.tweaks.mixin.accessors.ItemAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements ItemGroupAccess {
    private final List<Item> ITEMS = new ArrayList<>();

    @Inject(at = @At("HEAD"), method = "appendStacks", cancellable = true)
    private void append(DefaultedList<ItemStack> stacks, CallbackInfo ci) {
        for (Item item : ITEMS) {
            item.appendStacks((ItemGroup) (Object) this, stacks);
        }
        ci.cancel();
    }

    @Unique
    @Override
    public void mTweaks$initItems() {
        for (Item item : Registry.ITEM) {
            if (((ItemAccess)item).isIn((ItemGroup) (Object) this) || item == Items.ENCHANTED_BOOK) {
                ITEMS.add(item);
            }
        }
        for (Table.Cell<Item, ItemGroup, Item> cell : Tweaks.ITEM_GROUP_OVERRIDES.cellSet()) {
            if (cell.getColumnKey() == (ItemGroup) (Object) this) {
                ITEMS.remove(cell.getRowKey());
                int i = ITEMS.indexOf(cell.getValue());
                ITEMS.add(i + 1, cell.getRowKey());
            }
        }
    }
}
