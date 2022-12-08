package me.melontini.tweaks.util;

import me.melontini.tweaks.config.TweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO move to CrackerLog
@Deprecated(since = "4.5")
public class LogUtil {
    private static final String MODID = "m-tweaks";
    private static final Logger LOGGER = LogManager.getLogger("m-tweaks");

    private static final TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();

    private static final boolean dev = FabricLoader.getInstance().isDevelopmentEnvironment() || CONFIG.debugMessages;

    public static void info(String string) {
        if (dev) LOGGER.info("[" + MODID + "] " + string);
    }
    public static void info(Object object) {
        if (dev) LOGGER.info("[" + MODID + "] " + object);
    }
    public static void info(String string, Object... params) {
        if (dev) LOGGER.info("[" + MODID + "] " + string, params);
    }
}
