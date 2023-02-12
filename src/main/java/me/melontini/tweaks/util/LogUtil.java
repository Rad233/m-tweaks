package me.melontini.tweaks.util;

import me.melontini.crackerutil.CrackerLog;
import me.melontini.tweaks.config.TweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil extends CrackerLog {
    private static final Logger LOGGER = LogManager.getLogger("m-tweaks");//TODO change this in CrackerLog

    private static final TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();

    private static final boolean dev = FabricLoader.getInstance().isDevelopmentEnvironment() || CONFIG.debugMessages;

    public static void devInfo(String msg) {
        if (dev) {
            String name = "[" + getCallerName() + "] ";
            LOGGER.info(name + msg);
        }
    }
    public static void devInfo(Object object) {
        if (dev) {
            String name = "[" + getCallerName() + "] ";
            LOGGER.info(name + object);
        }
    }
    public static void devInfo(String msg, Object... params) {
        if (dev) {
            String name = "[" + getCallerName() + "] ";
            LOGGER.info(name + msg, params);
        }
    }
}
