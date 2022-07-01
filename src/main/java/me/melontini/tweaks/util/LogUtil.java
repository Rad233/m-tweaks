package me.melontini.tweaks.util;

import me.melontini.tweaks.config.TweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {
    //TODO
    private static final Logger LOGGER = LogManager.getLogger("m-tweaks");

    private static final TweaksConfig config = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();

    private static final boolean dev = FabricLoader.getInstance().isDevelopmentEnvironment() || config.debugMessages;

    public static void info(String string) {
        if (dev) LOGGER.info("[m-tweaks] " + string);
    }
    public static void info(Object object) {
        if (dev) LOGGER.info("[m-tweaks] " + object);
    }

    public static void importantInfo(String string) {
        LOGGER.info("[m-tweaks] " + string);
    }

    public static void importantInfo(Object object) {
        LOGGER.info("[m-tweaks] " + object);
    }


    public static void warn(String string) {
        LOGGER.warn("[m-tweaks] " + string);
    }

    public static void warn(Object object) {
        LOGGER.warn("[m-tweaks] " + object);
    }

    public static void error(String string) {
        LOGGER.error("[m-tweaks] " + string);
    }

    public static void error(Object object) {
        LOGGER.error("[m-tweaks] " + object);
    }
}
