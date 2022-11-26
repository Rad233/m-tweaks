package me.melontini.tweaks.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;

public class NBTUtil {
    public static NbtCompound writeInventoryToNbt(NbtCompound nbt, Inventory inventory) {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                nbtList.add(itemStack.writeNbt(NbtBuilder.create().putByte("Slot", (byte) i).build()));
            }
        }
        nbt.put("Items", nbtList);
        return nbt;
    }

    public static void readInventoryFromNbt(NbtCompound nbt, Inventory inventory) {
        if (nbt != null) if (nbt.getList("Items", NbtElement.COMPOUND_TYPE) != null) {
            NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte("Slot") & 255;
                //noinspection ConstantConditions
                if (j >= 0 && j < inventory.size()) {
                    inventory.setStack(j, ItemStack.fromNbt(nbtCompound));
                }
            }
        }
    }

    public static int getInt(NbtCompound nbt, String name, int defaultValue) {
        if (nbt != null) {
            return nbt.getInt(name);
        }
        return defaultValue;
    }

    public static int getInt(NbtCompound nbt, String name, int min, int max) {
        if (nbt != null) {
            int i = nbt.getInt(name);
            return MathHelper.clamp(i, min, max);
        }
        return min;
    }
}
