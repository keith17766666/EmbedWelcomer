package dev.keith.bots;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class Config {
    static {
        token = "";
        debug = false;
        activity = "";
        type = null;
        status = OnlineStatus.ONLINE;
        try (BufferedReader reader = new BufferedReader(new FileReader("bot.config"))) {
            reader.lines().forEach(
                    s -> {
                        if (s.startsWith("#")) {
                            return;
                        }
                        if (s.startsWith("token=")) {
                            token = s.replace("token=", "");
                        }
                        if (s.startsWith("debug=")) {
                            debug = Boolean.parseBoolean(s.replace("debug=", ""));
                        }
                        if (s.startsWith("activity=")) {
                            activity = s.replace("activity=", "");
                        }
                        if (s.startsWith("activity_type=")) {
                            type = Activity.ActivityType.valueOf(s.replace("activity_type=", "")
                                    .replace(" ", "")
                                    .toUpperCase());
                        }
                        if (s.startsWith("status=")) {
                            status = parseStatus(s.replace("status=", "").toLowerCase());
                        }
                    }
            );
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(Config.class.getSimpleName()).warn("No config file is detected, using default setting.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static OnlineStatus parseStatus(String s) {
        return switch (s) {
            case "invisible" -> OnlineStatus.INVISIBLE;
            case "dnd", "do_not_disturb" -> OnlineStatus.DO_NOT_DISTURB;
            case "idle" -> OnlineStatus.IDLE;
            default -> OnlineStatus.ONLINE;
        };
    }

    public static String token;
    public static boolean debug;
    public static String activity;
    public static Activity.ActivityType type;
    public static OnlineStatus status;
}
