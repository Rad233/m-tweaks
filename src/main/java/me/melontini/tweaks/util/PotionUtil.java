package me.melontini.tweaks.util;

import me.melontini.tweaks.Tweaks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PotionUtil {
    public static StatusEffect getStatusEffect(Identifier id) {
        if (Registry.STATUS_EFFECT.get(id) != null) {
            return Registry.STATUS_EFFECT.get(id);
        } else {
            LogUtil.error("Invalid Status effect Identifier provided! " + id);
            return null;
        }
    }
}
