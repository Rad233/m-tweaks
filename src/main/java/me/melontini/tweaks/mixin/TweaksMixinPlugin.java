package me.melontini.tweaks.mixin;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TweaksMixinPlugin implements IMixinConfigPlugin {
    Map<String, Boolean> OPTION_MAP = new HashMap<>();
    private TweaksConfig CONFIG;

    @Override
    public void onLoad(String mixinPackage) {
        AutoConfig.register(TweaksConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (CONFIG.compatMode) LogUtil.warn("Compat mode is on!");
        OPTION_MAP.put("blocks.better_fletching_table", CONFIG.usefulFletching);
        OPTION_MAP.put("blocks.campfire_effects", CONFIG.campfireTweaks.campfireEffects);
        OPTION_MAP.put("blocks.leaf_tweak", CONFIG.leafSlowdown);

        OPTION_MAP.put("entities.baiting_villagers", CONFIG.villagersFollowEmeraldBlocks);
        OPTION_MAP.put("entities.flower_duplication", CONFIG.beeFlowerDuplication);
        OPTION_MAP.put("entities.furnace_minecart", CONFIG.betterFurnaceMinecart);

        OPTION_MAP.put("items.cart_copy", CONFIG.minecartBlockPicking);
        OPTION_MAP.put("items.mending_fix", CONFIG.balancedMending);
        OPTION_MAP.put("items.wandering_trader", CONFIG.tradingGoatHorn);
        OPTION_MAP.put("items.clock_tooltip", CONFIG.tradingGoatHorn);

        OPTION_MAP.put("world.crop_temperature", CONFIG.temperatureBasedCropGrowthSpeed);
        OPTION_MAP.put("world.epic_fire", CONFIG.quickFire);
        OPTION_MAP.put("world.falling_beehives", CONFIG.canBeeNestsFall);

        OPTION_MAP.put("misc.minor_inconvenience", CONFIG.minorInconvenience);
        OPTION_MAP.put("misc.unknown", CONFIG.unknown);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var path = "me.melontini.tweaks.mixin.";
        if (CONFIG.compatMode) {
            //pretty cursed
            for (Map.Entry<String, Boolean> entry : OPTION_MAP.entrySet()) {
                if (mixinClassName.startsWith(path + entry.getKey())) {
                    LogUtil.info("Checking {}, {}", mixinClassName, entry.getValue());
                    return entry.getValue();
                }
            }
        }
        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
