package me.melontini.tweaks.mixin;

import me.melontini.crackerutil.util.mixin.ExtendedPlugin;
import me.melontini.tweaks.config.TweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TweaksMixinPlugin extends ExtendedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("TweaksMixinPlugin");
    private static final String MIXIN_TO_OPTION_ANNOTATION = "Lme/melontini/tweaks/util/annotations/MixinRelatedConfigOption;";
    private TweaksConfig CONFIG;
    private static boolean log;

    static {
        LOGGER.info("({}) Definitely up to a lot of good", LOGGER.getName());
    }

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);
        AutoConfig.register(TweaksConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
        log = CONFIG.debugMessages || FabricLoader.getInstance().isDevelopmentEnvironment();

        if (CONFIG.compatMode) {
            LOGGER.warn("({}) Compat mode is on!", LOGGER.getName());
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
                        if (MIXIN_TO_OPTION_ANNOTATION.equals(node1.desc)) {
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
        }
        if (log) LOGGER.info("({}) {} : {}", LOGGER.getName(), mixinClassName.replaceFirst("me\\.melontini\\.tweaks\\.mixin\\.", ""), load ? "loaded" : "not loaded");
        return load;
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        super.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);
        if (targetClass.visibleAnnotations != null && !targetClass.visibleAnnotations.isEmpty()) {//strip our annotation from the class
            targetClass.visibleAnnotations.removeIf(node -> MIXIN_TO_OPTION_ANNOTATION.equals(node.desc));
        }
    }
}
