package me.melontini.tweaks.util;

import me.melontini.crackerutil.util.MakeSure;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class PotionUtil {
    public static @NotNull StatusEffect getStatusEffect(Identifier id) {
        StatusEffect effect = Registries.STATUS_EFFECT.get(id);
        MakeSure.notNull(effect, "[m-tweaks] Couldn't get StatusEffect from identifier: " + id);
        return effect;
    }
}
