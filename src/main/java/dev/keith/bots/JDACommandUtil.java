package dev.keith.bots;

import dev.keith.bots.util.EmbedConstructor;
import dev.keith.bots.util.PlaceHolderConverter;
import dev.keith.database.entity.LeaveEmbedMessageEntity;
import dev.keith.database.entity.LeaveMessageEntity;
import dev.keith.database.entity.WelcomeEmbedMessageEntity;
import dev.keith.database.entity.WelcomeMessageEntity;
import dev.keith.database.helpers.LeaveEmbedMessageSQLHelper;
import dev.keith.database.helpers.LeaveMessageSQLHelper;
import dev.keith.database.helpers.WelcomeEmbedMessageSQLHelper;
import dev.keith.database.helpers.WelcomeMessageSQLHelper;
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
import static dev.keith.bots.util.PlaceHolderConverter.convert;

public class JDACommandUtil extends ListenerAdapter {
    private static final WelcomeEmbedMessageSQLHelper welcome_embed =
            new WelcomeEmbedMessageSQLHelper();
    private static final WelcomeMessageSQLHelper welcome_message =
            new WelcomeMessageSQLHelper();
    private static final LeaveEmbedMessageSQLHelper leave_embed =
            new LeaveEmbedMessageSQLHelper();
    private static final LeaveMessageSQLHelper leave_message =
            new LeaveMessageSQLHelper();

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
            long serverId = Objects.requireNonNull(event.getGuild()).getIdLong();
            switch (event.getName()) {
                case WELCOME_EMBED_COMMAND: {
                    long channelId = Objects.requireNonNull(event.getOption("welcome_channel"))
                            .getAsChannel().getIdLong();
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
                    WelcomeEmbedMessageEntity entity = WelcomeEmbedMessageEntity.fromEmbedConstructor
                            (serverId, channelId, builder.build());
                    welcome_embed.storeIfAbsent(serverId, entity);
                    event.getHook().sendMessage("Done setting up the welcome message!").queue();
                    break;
                }
                case WELCOME_COMMAND: {
                    long channelId = Objects.requireNonNull(event.getOption("welcome_channel"))
                            .getAsChannel().getIdLong();
                    WelcomeMessageEntity entity =
                            new WelcomeMessageEntity(serverId, channelId,
                                    Objects.requireNonNull(event.getOption("message")).getAsString());
                    welcome_message.storeIfAbsent(serverId, entity);
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
                    long channelId = Objects.requireNonNull(event.getOption("leave_channel"))
                            .getAsChannel().getIdLong();
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
                    LeaveEmbedMessageEntity entity =
                            LeaveEmbedMessageEntity.fromEmbedConstructor(serverId, channelId, builder.build());
                    leave_embed.storeIfAbsent(serverId, entity);
                    event.getHook().sendMessage("Done setting up the leaving message!").queue();
                    break;
                }
                case LEAVE_COMMAND: {
                    long channelId = Objects.requireNonNull(event.getOption("leave_channel"))
                            .getAsChannel().getIdLong();
                    LeaveMessageEntity entity =
                            new LeaveMessageEntity(serverId, channelId,
                                    Objects.requireNonNull(event.getOption("message")).getAsString());
                    leave_message.storeIfAbsent(serverId, entity);
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
    }
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        sendWelcomeMessage(event);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        sendLeaveMessage(event);
    }
    private void sendWelcomeMessage(GuildMemberJoinEvent event) {
        long serverId = Objects.requireNonNull(event.getGuild()).getIdLong();
        if (welcome_message.query(serverId) != null) {
            sentNormalWelcomeMessage(event, welcome_message.query(serverId));
        }
        if (welcome_embed.query(serverId) != null) {
            sentWelcomeEmbedMessage(event, welcome_embed.query(serverId));
        }
    }
    private void sendLeaveMessage(GuildMemberRemoveEvent event) {
        long serverId = Objects.requireNonNull(event.getGuild()).getIdLong();
        if (leave_message.query(serverId) != null) {
            sentNormalLeaveMessage(event, leave_message.query(serverId));
        }
        if (leave_embed.query(serverId) != null) {
            sentLeaveEmbedMessage(event, leave_embed.query(serverId));
        }
    }
    private void sentNormalWelcomeMessage(GuildMemberJoinEvent event, WelcomeMessageEntity entity) {
        String toSend = convert(event, entity.getMessage());
        TextChannel channel = event.getGuild().getTextChannelById(entity.getChannel_id());
        if (channel != null) {
            channel.sendMessage(toSend).queue();
        }
    }
    private void sentNormalLeaveMessage(GuildMemberRemoveEvent event, LeaveMessageEntity entity) {
        String toSend = convert(event, entity.getMessage());
        TextChannel channel = event.getGuild().getTextChannelById(entity.getChannel_id());
        if (channel != null) {
            channel.sendMessage(toSend).queue();
        }
    }
    private void sentWelcomeEmbedMessage(GuildMemberJoinEvent event, WelcomeEmbedMessageEntity entity) {
        EmbedConstructor embed = entity.toEmbedConstructor();
        MessageEmbed toSend = embed.construct(event, PlaceHolderConverter::convert);
        TextChannel channel = event.getGuild().getTextChannelById(entity.getChannel_id());
        if (channel != null) {
            assert toSend != null;
            channel.sendMessageEmbeds(toSend).queue();
        }
    }
    private void sentLeaveEmbedMessage(GuildMemberRemoveEvent event, LeaveEmbedMessageEntity entity) {
        EmbedConstructor embed = entity.toEmbedConstructor();
        MessageEmbed toSend = embed.construct(event, PlaceHolderConverter::convert);
        TextChannel channel = event.getGuild().getTextChannelById(entity.getChannel_id());
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
                        .addOption(OptionType.STRING, "descriptions", "The descriptions of the welcome Message,If you want multiple lines, split it with \";\"", true)
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
