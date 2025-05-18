package dev.keith.bots.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EmbedConstructor {
    private final String title;
    private final List<String> descriptions;
    private final String attachment;
    private final Color color;

    private EmbedConstructor(String title, List<String> descriptions, String attachment, Color color) {
        this.title = title;
        this.descriptions = descriptions;
        this.attachment = attachment;
        this.color = color;
    }

    public MessageEmbed construct(GuildMemberJoinEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(title
                // Placeholders
                .replace("%ping_member%", event.getMember().getAsMention())
                .replace("%member%", event.getMember().getEffectiveName())
                .replace("%server_name%", event.getGuild().getName())
                .replace("%server_member_number%", String.valueOf(event.getGuild().getMemberCount()))
        );
        descriptions.stream()
                .map(s -> s
                        // Placeholders
                        .replace("%ping_member%", event.getMember().getAsMention())
                        .replace("%member%", event.getMember().getEffectiveName())
                        .replace("%server_name%", event.getGuild().getName())
                        .replace("%server_member_number%", String.valueOf(event.getGuild().getMemberCount())))
                .forEach(s -> {
                    builder.appendDescription(s + "\n");
                });
        if (attachment != null) {
            builder.setImage(attachment);
        }
        builder.setColor(color);
        return builder.build();
    }

    public MessageEmbed construct(GuildMemberRemoveEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(title
                // Placeholders
                .replace("%user%", event.getUser().getEffectiveName())
                .replace("%server_name%", event.getGuild().getName())
                .replace("%server_member_number%", String.valueOf(event.getGuild().getMemberCount()))
        );
        descriptions.stream()
                .map(s -> s
                        // Placeholders
                        .replace("%user%", event.getUser().getEffectiveName())
                        .replace("%server_name%", event.getGuild().getName())
                        .replace("%server_member_number%", String.valueOf(event.getGuild().getMemberCount())))
                .map(s -> s + "\n")
                .forEach(builder::appendDescription);
        if (attachment != null) {
            builder.setImage(attachment);
        }
        builder.setColor(color);
        return builder.build();
    }

    @Override
    public String toString() {
        return "EmbedConstructor[title:" + title + ",descriptions:" + descriptions + "]";
    }

    public static class Builder {
        private String title = "";
        private final List<String> descriptions = new ArrayList<>();
        private String image;
        private Color color = null;
        public static final Builder EMPTY = new Builder();

        public Builder title(String s) {
            this.title = s;
            return this;
        }
        public Builder appendDescription(String desc) {
            descriptions.add(desc);
            return this;
        }
        public Builder image(Message.Attachment attachment) {
            image = attachment.getUrl();
            return this;
        }
        public Builder image(String uri) {
            image = uri;
            return this;
        }
        public Builder color(Color color) {
            this.color = color;
            return this;
        }
        public EmbedConstructor build() {
            return new EmbedConstructor(title, descriptions, image, color);
        }
        public boolean isEmpty() {
            return title.isEmpty() && descriptions.isEmpty();
        }
    }
}
