package dev.keith.bots.util;

import dev.keith.bots.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);
    public static void logDebug(Logger log, String msg, Object... args) {
        if (Config.debug) {
            log.info(msg, args);
        }
    }
}
