package me.melontini.tweaks.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

//quick builder to make NBTs in one line
public class NbtBuilder {
    private NbtCompound nbt = new NbtCompound();

    public static NbtBuilder create() {
        return new NbtBuilder();
    }

    public static NbtBuilder create(NbtCompound nbt) {
        var b = new NbtBuilder();
        if (nbt != null) b.nbt = nbt;
        return b;
    }

    public NbtBuilder put(String key, NbtElement element) {
        nbt.put(key, element);
        return this;
    }

    public Pair<NbtBuilder, @Nullable NbtElement> put1(String key, NbtElement element) {
        return new Pair<>(this, nbt.put(key, element));
    }

    public NbtBuilder putByte(String key, byte value) {
        nbt.putByte(key, value);
        return this;
    }

    public NbtBuilder putShort(String key, short value) {
        nbt.putShort(key, value);
        return this;
    }

    public NbtBuilder putInt(String key, int value) {
        nbt.putInt(key, value);
        return this;
    }

    public NbtBuilder putLong(String key, long value) {
        nbt.putLong(key, value);
        return this;
    }

    public NbtBuilder putUuid(String key, UUID value) {
        nbt.putUuid(key, value);
        return this;
    }

    public NbtBuilder putFloat(String key, float value) {
        nbt.putFloat(key, value);
        return this;
    }

    public NbtBuilder putDouble(String key, double value) {
        nbt.putDouble(key, value);
        return this;
    }

    public NbtBuilder putString(String key, String value) {
        nbt.putString(key, value);
        return this;
    }

    public NbtBuilder putByteArray(String key, byte[] value) {
        nbt.putByteArray(key, value);
        return this;
    }

    public NbtBuilder putByteArray(String key, List<Byte> value) {
        nbt.putByteArray(key, value);
        return this;
    }

    public NbtBuilder putIntArray(String key, int[] value) {
        nbt.putIntArray(key, value);
        return this;
    }

    public NbtBuilder putIntArray(String key, List<Integer> value) {
        nbt.putIntArray(key, value);
        return this;
    }

    public NbtBuilder putLongArray(String key, long[] value) {
        nbt.putLongArray(key, value);
        return this;
    }

    public NbtBuilder putLongArray(String key, List<Long> value) {
        nbt.putLongArray(key, value);
        return this;
    }

    public NbtBuilder putBoolean(String key, boolean value) {
        nbt.putBoolean(key, value);
        return this;
    }

    public NbtCompound build() {
        return nbt;
    }
}
