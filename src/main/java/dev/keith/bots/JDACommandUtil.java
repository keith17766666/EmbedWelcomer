package dev.keith.bots;

import dev.keith.bots.util.EmbedConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;

import static dev.keith.bots.util.LoggerUtil.logDebug;

public class JDACommandUtil extends ListenerAdapter {
    private static final Map<Long, Long> welcome_channel_id = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, EmbedConstructor> welcome_embed = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, Long> leave_channel_id = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, EmbedConstructor> leave_embed = Collections.synchronizedMap(new HashMap<>());
    private static final Logger LOGGER = LoggerFactory.getLogger(JDACommandUtil.class);
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        logDebug(LOGGER, "User {} called /{} command!", event.getUser().getEffectiveName(), event.getName());
        long start = 0;
        if (Config.debug) {
            start = System.nanoTime();
        }
        if (!event.getUser().isBot()) {
            switch (event.getName()) {
                case "welcome": {
                    putIfPresent(welcome_channel_id, Objects.requireNonNull(event.getGuild()).getIdLong(),
                            Objects.requireNonNull(event.getOption("welcome_channel"))
                            .getAsChannel().getIdLong());
                    EmbedConstructor.Builder builder = new EmbedConstructor.Builder()
                            .color(getColor(event.getOption("color").getAsString()))
                            .title(event.getOption("title").getAsString());
                    if (event.getOption("descriptions") != null) {
                        for (String desc : event.getOption("descriptions").getAsString().split(";")) {
                            builder.appendDescription(desc);
                        }
                    }
                    if (event.getOption("image") != null) {
                        builder.image(event.getOption("image").getAsAttachment());
                    }
                    if (!builder.isEmpty()) {
                        putIfPresent(welcome_embed, event.getGuild().getIdLong(), builder.build());
                    }
                    event.getHook().sendMessage("Done setting up the welcome message!").queue();
                    break;
                }
                case "welcome-placeholder": {
                    event.getHook().sendMessage("""
                            Placeholder for /welcome:
                            %ping_member% -> Ping the joining member.
                            %member% -> Show the effective name for joining member
                            %server_name% -> Show the server name
                            %server_member_number% -> Show the server's member count
                           \s
                            Notice:\s
                            Due to limitation of discord, %ping_member% will not work for the title, 
                            and also will not actually ping the member.
                           \s""").queue();
                    break;
                }
                case "left": {
                    putIfPresent(leave_channel_id, Objects.requireNonNull(event.getGuild()).getIdLong(),
                            Objects.requireNonNull(event.getOption("left_channel"))
                                    .getAsChannel().getIdLong());
                    EmbedConstructor.Builder builder = new EmbedConstructor.Builder()
                            .color(getColor(event.getOption("color").getAsString()))
                            .title(event.getOption("title").getAsString());
                    if (event.getOption("descriptions") != null) {
                        for (String desc : event.getOption("descriptions").getAsString().split(";")) {
                            builder.appendDescription(desc);
                        }
                    }
                    if (event.getOption("image") != null) {
                        builder.image(event.getOption("image").getAsAttachment());
                    }
                    if (!builder.isEmpty()) {
                        putIfPresent(leave_embed, event.getGuild().getIdLong(), builder.build());
                    }
                    event.getHook().sendMessage("Done setting up the leaving message!").queue();
                    break;
                }
                case "left-placeholder": {
                    event.getHook().sendMessage("""
                            Placeholder for /left:
                            %user% -> Show the effective name for leaving user
                            %server_name% -> Show the server name
                            %server_member_number% -> Show the server's member count
                            """).queue();
                    break;
                }
            }
        }
        if (start != 0) {
            logDebug(LOGGER, "Command /{} took {}ms to done.", event.getName(), (System.nanoTime() - start)/10000);
        }
    }

    public static Color getColor(String color) {
        try {
            return (Color) Color.class.getField(color).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Unknown Color: " + color);
        }
    }

    public static void doRegister(JDA jda) {
        jda.getGuilds().forEach(JDACommandUtil::doRegister);
        jda.updateCommands().queue();
    }
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (welcome_embed.containsKey(event.getGuild().getIdLong())) {
            MessageEmbed embed = welcome_embed.get(event.getGuild().getIdLong()).construct(event);
            TextChannel welcomeChannel = event.getGuild().getTextChannelById(welcome_channel_id.get(event.getGuild().getIdLong()));
            if (welcomeChannel != null) {
                welcomeChannel.sendMessageEmbeds(embed).queue();
            }
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (leave_embed.containsKey(event.getGuild().getIdLong())) {
            MessageEmbed embed = leave_embed.get(event.getGuild().getIdLong()).construct(event);
            TextChannel leaveChannel = event.getGuild().getTextChannelById(leave_channel_id.get(event.getGuild().getIdLong()));
            if (leaveChannel != null) {
                leaveChannel.sendMessageEmbeds(embed).queue();
            }
        }
    }

    public static void doRegister(Guild guild) {
        guild.updateCommands().queue(); // clear all commands
        guild.updateCommands()
                .addCommands(Commands.slash("welcome", "Setup Welcome Messages and channel.")
                        .addOption(OptionType.CHANNEL, "welcome_channel", "The Welcome Channel", true)
                        .addOption(OptionType.STRING, "title", "The Title of the welcome Message", true)
                        .addOptions(new OptionData(OptionType.STRING, "color", "The color of the embed", true)
                                .addChoice("White", "white")
                                .addChoice("Light Gray", "lightGray")
                                .addChoice("Gray", "gray")
                                .addChoice("Dark Gray", "darkGray")
                                .addChoice("Black", "black")
                                .addChoice("Red", "red")
                                .addChoice("Pink", "pink")
                                .addChoice("Orange", "orange")
                                .addChoice("Yellow", "yellow")
                                .addChoice("Green", "green")
                                .addChoice("Magenta", "magenta")
                                .addChoice("Cyan", "cyan")
                                .addChoice("Blue", "blue")
                        )
                        .addOption(OptionType.STRING, "descriptions", "The descriptions of the welcome Message,If you want multiple lines, split it with \";\", Optional")
                        .addOption(OptionType.ATTACHMENT, "image", "The image you want for the embed, Optional")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL)))
                .addCommands(Commands.slash("welcome-placeholder", "Show Placeholder available for /welcome")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL)))
                // leave
                .addCommands(Commands.slash("left", "Setup Left Messages and channel.")
                        .addOption(OptionType.CHANNEL, "left_channel", "The Left Channel", true)
                        .addOption(OptionType.STRING, "title", "The Title of the left Message", true)
                        .addOptions(new OptionData(OptionType.STRING, "color", "The color of the embed", true)
                                .addChoice("White", "white")
                                .addChoice("Light Gray", "lightGray")
                                .addChoice("Gray", "gray")
                                .addChoice("Dark Gray", "darkGray")
                                .addChoice("Black", "black")
                                .addChoice("Red", "red")
                                .addChoice("Pink", "pink")
                                .addChoice("Orange", "orange")
                                .addChoice("Yellow", "yellow")
                                .addChoice("Green", "green")
                                .addChoice("Magenta", "magenta")
                                .addChoice("Cyan", "cyan")
                                .addChoice("Blue", "blue")
                        )
                        .addOption(OptionType.STRING, "descriptions", "The descriptions of the left Message,If you want multiple lines, split it with \";\", Optional")
                        .addOption(OptionType.ATTACHMENT, "image", "The image you want for the embed, Optional")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL)))
                .addCommands(Commands.slash("left-placeholder", "Show Placeholder available for /left")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL)))
                .queue(list -> LOGGER.info("Command register done for guild {}", guild.getName()));
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        doRegister(event.getGuild());
    }
    private <K, V> void putIfPresent(Map <K, V> map, K key, V value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }
}
