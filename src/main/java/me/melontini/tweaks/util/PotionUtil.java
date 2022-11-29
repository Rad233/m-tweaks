package me.melontini.tweaks.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class PotionUtil {
    public static @Nullable StatusEffect getStatusEffect(Identifier id) {
        if (Registries.STATUS_EFFECT.get(id) != null) {
            return Registries.STATUS_EFFECT.get(id);
        } else {
            throw new NullPointerException("[m-tweaks] Couldn't get StatusEffect from identifier: " + id);
        }
    }
}
