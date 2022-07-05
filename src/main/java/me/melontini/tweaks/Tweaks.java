package me.melontini.tweaks;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.networks.ServerSideNetworking;
import me.melontini.tweaks.registries.EntityTypeRegistry;
import me.melontini.tweaks.registries.ItemRegistry;
import me.melontini.tweaks.registries.ResourceConditionRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.UUID;

public class Tweaks implements ModInitializer {

    public static final EntityAttributeModifier LEAF_SLOWNESS = new EntityAttributeModifier(UUID.fromString("f72625eb-d4c4-4e1d-8e5c-1736b9bab349"), "Leaf Slowness", -0.3, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    public static final String MODID = "m-tweaks";
    public static TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();

    @Override
    public void onInitialize() {
        ItemRegistry.register();
        EntityTypeRegistry.register();
        ServerSideNetworking.register();
        ResourceConditionRegistry.register();
    }
}
