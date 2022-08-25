package me.melontini.tweaks.mixin;

import me.melontini.tweaks.config.TweaksConfig;
import me.melontini.tweaks.util.LogUtil;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.util.*;

public class TweaksMixinPlugin implements IMixinConfigPlugin {
    Map<String, Boolean> OPTION_MAP = new HashMap<>();
    private TweaksConfig CONFIG;

    @Override
    public void onLoad(String mixinPackage) {
        AutoConfig.register(TweaksConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        if (CONFIG.compatMode) {
            LogUtil.warn("Compat mode is on!");
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (CONFIG.compatMode) {
            try {
                ClassReader reader = new ClassReader(mixinClassName);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                if (node.visibleAnnotations != null) {
                    for (AnnotationNode node1 : node.visibleAnnotations) {
                        if (node1.desc.equals("Lme/melontini/tweaks/util/annotations/MixinRelatedConfigOption;")) {
                            List<Boolean> booleans = new ArrayList<>();
                            for (int i = 0; i < node1.values.size(); i++) {
                                if (i % 2 == 1) {
                                    String configOption = (String) node1.values.get(i);//probably fine?
                                    List<String> classes = Arrays.stream(configOption.split("\\.")).toList();
                                    boolean j;
                                    if (classes.size() == 2) {
                                        j = CONFIG.getClass().getField(classes.get(0)).get(CONFIG).getClass().getField(classes.get(1)).getBoolean(CONFIG.getClass().getField(classes.get(0)).get(CONFIG));
                                    } else {
                                        j = CONFIG.getClass().getField(configOption).getBoolean(CONFIG);
                                    }
                                    booleans.add(j);
                                }
                            }
                            LogUtil.info("{} : {}", mixinClassName, booleans.stream().allMatch(aBoolean -> aBoolean) ? "loaded" : "not loaded");
                            return booleans.stream().allMatch(aBoolean -> aBoolean);
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
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
