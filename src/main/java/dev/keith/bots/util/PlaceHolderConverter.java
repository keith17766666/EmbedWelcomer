package dev.keith.bots.util;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public class PlaceHolderConverter {
    public static final String PING_MEMBER = "%ping_member%";
    public static final String MEMBER_NAME = "%member%";
    public static final String SERVER_NAME = "%server_name%";
    public static final String SERVER_MEMBER_NUMBER = "%server_member_number%";
    public static final String USER_NAME = "%user%";
    public static String convert(GuildMemberJoinEvent event, String msg) {
        return msg.replace(PING_MEMBER, event.getMember().getAsMention())
                .replace(MEMBER_NAME, event.getMember().getEffectiveName())
                .replace(SERVER_NAME, event.getGuild().getName())
                .replace(SERVER_MEMBER_NUMBER, String.valueOf(event.getGuild().getMemberCount()));
    }
    public static String convert(GuildMemberRemoveEvent event, String msg) {
        return msg.replace(USER_NAME, event.getUser().getEffectiveName())
                .replace(SERVER_NAME, event.getGuild().getName())
                .replace(SERVER_MEMBER_NUMBER, String.valueOf(event.getGuild().getMemberCount()));
    }
}
