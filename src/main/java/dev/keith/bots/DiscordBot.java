package dev.keith.bots;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

import static dev.keith.bots.util.LoggerUtil.logDebug;

public class DiscordBot {
    public static JDA JDA;
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class.getSimpleName());
    public static void startBot(String token) {
        long nano = System.nanoTime();
        LOGGER.info("Starting The Bot...");
        try {
            JDA = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS))
                    .addEventListeners(new JDACommandUtil())
                    .setActivity(Activity.of(Config.type == null ? Activity.ActivityType.PLAYING : Config.type,
                            Config.activity.isEmpty() ? "Integrated Discord Bot but with placeholder!" : Config.activity))
                    .setStatus(Config.status)
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Invite Link: {}", JDA.getInviteUrl(Permission.ADMINISTRATOR).replace("scope=bot", "scope=bot+applications.commands"));
        JDACommandUtil.doRegister(JDA);
        logDebug(LOGGER, "Done all settings for {}!", JDA.getSelfUser().getName());
        System.out.println("Successfully finished startup!");
        logDebug(LOGGER, "Bot started in {}", System.nanoTime() - nano);
    }
}