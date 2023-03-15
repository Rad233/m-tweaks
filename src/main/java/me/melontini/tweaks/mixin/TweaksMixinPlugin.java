package me.melontini.tweaks.mixin;

import me.melontini.crackerutil.CrackerLog;
import me.melontini.crackerutil.util.mixin.ExtendedPlugin;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.*;

public class TweaksMixinPlugin extends ExtendedPlugin {
    private TweaksConfig CONFIG;

    @Override
    public void onLoad(String mixinPackage) {
        AutoConfig.register(TweaksConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (CONFIG.compatMode) {
            CrackerLog.warn("Compat mode is on!");
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean load = super.shouldApplyMixin(targetClassName, mixinClassName);
        if (CONFIG.compatMode && load) {
            try {
                //"inspired" by https://github.com/unascribed/Fabrication/blob/3.0/1.18/src/main/java/com/unascribed/fabrication/support/MixinConfigPlugin.java
                ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
                if (node.visibleAnnotations != null) {
                    for (AnnotationNode node1 : node.visibleAnnotations) {
                        if (node1.desc.equals("Lme/melontini/tweaks/util/annotations/MixinRelatedConfigOption;")) {
                            Map<String, Object> values = mapAnnotationNode(node1);
                            List<String> configOptions = (List<String>) values.get("value");
                            for (String configOption : configOptions) {
                                List<String> classes = Arrays.stream(configOption.split("\\.")).toList();

                                if (classes.size() > 1) {//🤯🤯🤯
                                    Object obj = TweaksConfig.class.getField(classes.get(0)).get(CONFIG);
                                    for (int i = 1; i < (classes.size() - 1); i++) {
                                        obj = obj.getClass().getField(classes.get(i)).get(obj);
                                    }
                                    load = obj.getClass().getField(classes.get(1)).getBoolean(obj);
                                } else {
                                    load = CONFIG.getClass().getField(configOption).getBoolean(CONFIG);
                                }
                                if (!load) break;
                            }
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            LogUtil.devInfo("{} : {}", mixinClassName, load ? "loaded" : "not loaded");
        }
        return load;
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
