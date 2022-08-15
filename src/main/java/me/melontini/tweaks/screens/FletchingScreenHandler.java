package me.melontini.tweaks.screens;

import me.melontini.tweaks.Tweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class FletchingScreenHandler extends ForgingScreenHandler {

    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Tweaks.FLETCHING_SCREEN_HANDLER, syncId, playerInventory, context);
    }

    @Override
    public boolean canTakeOutput(PlayerEntity player, boolean present) {
        return !this.output.isEmpty();
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        stack.onCraft(player.world, player, stack.getCount());
        this.output.unlockLastRecipe(player);
        this.decrementStack(0);
        this.decrementStack(1);
    }

    private void decrementStack(int slot) {
        ItemStack itemStack = this.input.getStack(slot);
        itemStack.decrement(1);
        this.input.setStack(slot, itemStack);
    }

    @Override
    public void updateResult() {
        ItemStack itemStack = this.input.getStack(0);
        NbtCompound oldNbt = itemStack.getNbt();
        int i = 0;
        if (!(itemStack.getItem() instanceof BowItem) || !(this.input.getStack(1).getItem() == Items.STRING)) {
            this.output.setStack(0, ItemStack.EMPTY);
        } else {
            if (oldNbt != null)
                i = oldNbt.getInt("MT-Tightened");
            if (i >= 32) return;

            ItemStack newStack = itemStack.copy();
            NbtCompound nbt;
            if (oldNbt != null) nbt = oldNbt.copy();
            else nbt = new NbtCompound();

            nbt.putInt("MT-Tightened", (int) (Math.min(i + Math.floor(Math.random() * (3) + 1), 32)));
            newStack.setNbt(nbt);
            this.output.setStack(0, newStack);
        }
    }

    @Override
    protected boolean canUse(BlockState state) {
        return state.isOf(Blocks.FLETCHING_TABLE);
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }
}
