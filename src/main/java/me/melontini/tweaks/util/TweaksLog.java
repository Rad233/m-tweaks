package me.melontini.tweaks.util;

import me.melontini.tweaks.config.TweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TweaksLog {
    private static final TweaksConfig CONFIG = AutoConfig.getConfigHolder(TweaksConfig.class).getConfig();
    private static final Logger LOGGER = LoggerFactory.getLogger("m-tweaks");
    private static final boolean debug = FabricLoader.getInstance().isDevelopmentEnvironment() || CONFIG.debugMessages;
    private static final boolean dev = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static void devInfo(String msg) {
        if (debug) {
            LOGGER.info(getName() + msg);
        }
    }
    public static void devInfo(Object object) {
        if (debug) {
            LOGGER.info(getName() + object);
        }
    }
    public static void devInfo(String msg, Object... params) {
        if (debug) {
            LOGGER.info(getName() + msg, params);
        }
    }

    public static void error(String msg) {
        LOGGER.error(getName() + msg);
    }

    public static void error(String msg, Throwable t) {
        LOGGER.error(getName() + msg, t);
    }

    public static void error(Object msg) {
        LOGGER.error(getName() + msg.toString());
    }

    public static void error(String msg, Object... args) {
        LOGGER.error(getName() + msg, args);
    }

    public static void warn(String msg) {
        LOGGER.warn(getName() + msg);
    }

    public static void warn(String msg, Throwable t) {
        LOGGER.warn(getName() + msg, t);
    }

    public static void warn(Object msg) {
        LOGGER.warn(getName() + msg.toString());
    }

    public static void warn(String msg, Object... args) {
        LOGGER.warn(getName() + msg, args);
    }

    public static void info(String msg) {
        LOGGER.info(getName() + msg);
    }

    public static void info(String msg, Throwable t) {
        LOGGER.info(getName() + msg, t);
    }

    public static void info(Object msg) {
        LOGGER.info(getName() + msg.toString());
    }

    public static void info(String msg, Object... args) {
        LOGGER.info(getName() + msg, args);
    }

    public static String getName() {
        return dev ? "" : "(" + LOGGER.getName() + ") ";
    }
}
