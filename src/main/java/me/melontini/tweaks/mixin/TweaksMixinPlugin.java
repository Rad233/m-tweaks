package me.melontini.tweaks.mixin;

import me.melontini.crackerutil.CrackerLog;
import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TweaksMixinPlugin implements IMixinConfigPlugin {
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
        if (CONFIG.compatMode) {
            try {
                //"inspired" by https://github.com/unascribed/Fabrication/blob/3.0/1.18/src/main/java/com/unascribed/fabrication/support/MixinConfigPlugin.java
                ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
                if (node.visibleAnnotations != null) {
                    for (AnnotationNode node1 : node.visibleAnnotations) {
                        if (node1.desc.equals("Lme/melontini/tweaks/util/annotations/MixinRelatedConfigOption;")) {
                            List<Boolean> booleans = new ArrayList<>();
                            List<String> configOptions = (List<String>) node1.values.get(1);
                            for (String configOption : configOptions) {
                                List<String> classes = Arrays.stream(configOption.split("\\.")).toList();
                                boolean j;
                                if (classes.size() > 1) {//🤯🤯🤯
                                    Object obj = TweaksConfig.class.getField(classes.get(0)).get(CONFIG);
                                    for (int i = 1; i < (classes.size() - 1); i++) {
                                        obj = obj.getClass().getField(classes.get(i)).get(obj);
                                    }
                                    j = obj.getClass().getField(classes.get(1)).getBoolean(obj);
                                } else {
                                    j = CONFIG.getClass().getField(configOption).getBoolean(CONFIG);
                                }
                                booleans.add(j);
                            }
                            LogUtil.devInfo("{} : {}", mixinClassName, booleans.stream().allMatch(aBoolean -> aBoolean) ? "loaded" : "not loaded");
                            return booleans.stream().allMatch(aBoolean -> aBoolean);
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
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
