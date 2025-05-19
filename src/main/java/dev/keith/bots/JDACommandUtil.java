package dev.keith.bots;

import dev.keith.bots.util.EmbedConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
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
import static dev.keith.bots.util.PlaceHolderConverter.convert;

public class JDACommandUtil extends ListenerAdapter {
    private static final Map<Long, Long> welcome_channel_id = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, EmbedConstructor> welcome_embed = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, String> welcome_message = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, Long> leave_channel_id = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, EmbedConstructor> leave_embed = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Long, String> leave_message = Collections.synchronizedMap(new HashMap<>());
    private static final Logger LOGGER = LoggerFactory.getLogger(JDACommandUtil.class);
    private static final DefaultMemberPermissions perm = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL);

    private static final String WELCOME_COMMAND = "welcome";
    private static final String WELCOME_EMBED_COMMAND = "welcome-embed";
    private static final String WELCOME_PLACEHOLDER_COMMAND = "welcome-placeholder";
    private static final String LEAVE_COMMAND = "leave";
    private static final String LEAVE_EMBED_COMMAND = "leave-embed";
    private static final String LEAVE_PLACEHOLDER_COMMAND = "leave-placeholder";

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
                case WELCOME_EMBED_COMMAND: {
                    putIfPresent(welcome_channel_id, Objects.requireNonNull(event.getGuild()).getIdLong(),
                            Objects.requireNonNull(event.getOption("welcome_channel"))
                                    .getAsChannel().getIdLong());
                    welcome_message.remove(event.getGuild().getIdLong());
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
                case WELCOME_COMMAND: {
                    putIfPresent(welcome_channel_id, Objects.requireNonNull(event.getGuild()).getIdLong(),
                            Objects.requireNonNull(event.getOption("welcome_channel"))
                                    .getAsChannel().getIdLong());
                    welcome_embed.remove(event.getGuild().getIdLong());
                    putIfPresent(welcome_message, event.getGuild().getIdLong(),
                            event.getOption("message").getAsString());
                    event.getHook().sendMessage("Done setting up the welcome message!").queue();
                    break;
                }
                case WELCOME_PLACEHOLDER_COMMAND: {
                    event.getHook().sendMessage("""
                            Placeholder for /welcome-embed and /welcome:
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
                case LEAVE_EMBED_COMMAND: {
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
                case LEAVE_COMMAND: {
                    putIfPresent(leave_channel_id, Objects.requireNonNull(event.getGuild()).getIdLong(),
                            Objects.requireNonNull(event.getOption("welcome_channel"))
                                    .getAsChannel().getIdLong());
                    leave_embed.remove(event.getGuild().getIdLong());
                    putIfPresent(welcome_message, event.getGuild().getIdLong(),
                            event.getOption("message").getAsString());
                    event.getHook().sendMessage("Done setting up the leaving message!").queue();
                    break;
                }
                case LEAVE_PLACEHOLDER_COMMAND: {
                    event.getHook().sendMessage("""
                            Placeholder for /leave and /leave-embed:
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
        sendWelcomeOrLeaveMessage(welcome_channel_id, welcome_message, welcome_embed, event);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        sendWelcomeOrLeaveMessage(leave_channel_id, leave_message, leave_embed, event);
    }
    private void sendWelcomeOrLeaveMessage(Map<Long, Long> channel_id, Map<Long, String> message, Map<Long, EmbedConstructor> embed, GenericGuildEvent event) {
        if (embed.containsKey(event.getGuild().getIdLong())) {
            sentEmbedMessage(channel_id, embed, event);
        } else if (message.containsKey(event.getGuild().getIdLong())) {
            sentNormalMessage(channel_id, message, event);
        }
    }
    private void sentNormalMessage(Map<Long, Long> channel_id, Map<Long, String> message, GenericGuildEvent event) {
        String toSend = "";
        if (event instanceof GuildMemberJoinEvent event1) {
            toSend = convert(event1, message.get(event.getGuild().getIdLong()));
        } else if (event instanceof GuildMemberRemoveEvent event1) {
            toSend = convert(event1, message.get(event.getGuild().getIdLong()));
        }

        TextChannel channel = event.getGuild().getTextChannelById(channel_id.get(event.getGuild().getIdLong()));
        if (channel != null) {
            channel.sendMessage(toSend).queue();
        }
    }
    private void sentEmbedMessage(Map<Long, Long> channel_id, Map<Long, EmbedConstructor> embed, GenericGuildEvent event) {
        MessageEmbed toSend = null;
        if (event instanceof GuildMemberJoinEvent event1) {
            toSend = embed.get(event.getGuild().getIdLong()).construct(event1);
        } else if (event instanceof GuildMemberRemoveEvent event1) {
            toSend = embed.get(event.getGuild().getIdLong()).construct(event1);
        }
        
        TextChannel channel = event.getGuild().getTextChannelById(channel_id.get(event.getGuild().getIdLong()));
        if (channel != null) {
            assert toSend != null;
            channel.sendMessageEmbeds(toSend).queue();
        }
    }

    public static void doRegister(Guild guild) {
        guild.updateCommands().queue(); // clear all commands
        guild.updateCommands()
                .addCommands(Commands.slash(WELCOME_EMBED_COMMAND, "Setup Embed Welcome Messages and channel.")
                        .addOption(OptionType.CHANNEL, "welcome_channel", "The Welcome Channel", true)
                        .addOption(OptionType.STRING, "title", "The Title of the welcome Message", true)
                        .addOptions(color(new OptionData(OptionType.STRING, "color", "The color of the embed", true)))
                        .addOption(OptionType.STRING, "descriptions", "The descriptions of the welcome Message,If you want multiple lines, split it with \";\", Optional")
                        .addOption(OptionType.ATTACHMENT, "image", "The image you want for the embed, Optional")
                        .setDefaultPermissions(perm))
                .addCommands(Commands.slash(WELCOME_COMMAND, "Setup Welcome Messages and channel.")
                        .addOption(OptionType.CHANNEL, "welcome_channel", "The welcome Channel", true)
                        .addOption(OptionType.STRING, "message", "The message.")
                        .setDefaultPermissions(perm))
                .addCommands(Commands.slash(WELCOME_PLACEHOLDER_COMMAND, "Show Placeholder available for /welcome and /welcome-embed")
                        .setDefaultPermissions(perm))
                // leave
                .addCommands(Commands.slash(LEAVE_EMBED_COMMAND, "Setup Embed Left Messages and channel.")
                        .addOption(OptionType.CHANNEL, "left_channel", "The Left Channel", true)
                        .addOption(OptionType.STRING, "title", "The Title of the left Message", true)
                        .addOptions(color(new OptionData(OptionType.STRING, "color", "The color of the embed", true)))
                        .addOption(OptionType.STRING, "descriptions", "The descriptions of the left Message,If you want multiple lines, split it with \";\", Optional")
                        .addOption(OptionType.ATTACHMENT, "image", "The image you want for the embed, Optional")
                        .setDefaultPermissions(perm))
                .addCommands(Commands.slash(LEAVE_COMMAND, "Setup Leave Messages and channel.")
                        .addOption(OptionType.CHANNEL, "left_channel", "The Left Channel", true)
                        .addOption(OptionType.STRING, "message", "The message.")
                        .setDefaultPermissions(perm))
                .addCommands(Commands.slash(LEAVE_PLACEHOLDER_COMMAND, "Show Placeholder available for /leave and /leave-embed")
                        .setDefaultPermissions(perm))
                .queue(list -> {
                    LOGGER.info("Command register done for guild {}", guild.getName());
                    list.forEach(c -> logDebug(LOGGER, "Command /{} has been registered!", c.getName()));
                });
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        doRegister(event.getGuild());
    }
    private static  <K, V> void putIfPresent(Map <K, V> map, K key, V value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }
    private static OptionData color(OptionData data) {
        return data.addChoice("White", "white")
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
                .addChoice("Blue", "blue");
    }
}
