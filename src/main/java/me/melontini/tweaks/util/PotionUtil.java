package me.melontini.tweaks.util;

import me.melontini.crackerutil.util.MakeSure;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public class PotionUtil {
    public static @NotNull StatusEffect getStatusEffect(Identifier id) {
        StatusEffect effect = Registry.STATUS_EFFECT.get(id);
        MakeSure.notNull(effect, "[m-tweaks] Couldn't get StatusEffect from identifier: " + id);
        return effect;
    }
}
