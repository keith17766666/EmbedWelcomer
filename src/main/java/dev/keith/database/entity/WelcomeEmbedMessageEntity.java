package dev.keith.database.entity;


import dev.keith.bots.util.EmbedConstructor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.Objects;

@Entity
@Table(name = "welcome_embed_message")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WelcomeEmbedMessageEntity {
    @Id
    private long server_id;
    @Column(name = "welcome_channel_id")
    private long channel_id;
    @Column(name = "welcome_embed_title")
    private String title;
    @Column(name = "welcome_embed_description")
    private String desc;
    @Column(name = "welcome_embed_color")
    private Color color;
    @Column(name = "welcome_embed_attachment")
    private String attachment;
    public static WelcomeEmbedMessageEntity fromEmbedConstructor
            (long server_id, long channel_id, final EmbedConstructor constructor) {
        String title = constructor.getTitle();
        StringBuilder descBuilder = new StringBuilder();
        constructor.getDescriptions().forEach(string -> {
            descBuilder.append(string);
            descBuilder.append(";");
        });
        Color color = constructor.getColor();
        String attachment = constructor.getAttachment();

        return new WelcomeEmbedMessageEntity(server_id, channel_id, title,
                descBuilder.toString(), color, attachment);
    }
    public EmbedConstructor toEmbedConstructor() {
        EmbedConstructor.Builder builder = new EmbedConstructor.Builder();
        builder = builder.title(title)
                .image(attachment)
                .color(color);
        for (String line : desc.split(";")) {
            builder = builder.appendDescription(line);
        }
        return builder.build();
    }
    public void modify(WelcomeEmbedMessageEntity newEntity) {
        if (!Objects.equals(newEntity.title, this.title)) {
            this.setTitle(newEntity.title);
        }
        if (!Objects.equals(newEntity.desc, this.desc)) {
            this.setDesc(desc);
        }
        if (!Objects.equals(newEntity.color, this.color)) {
            this.setColor(newEntity.color);
        }
        if (!Objects.equals(newEntity.attachment, this.attachment)) {
            this.setAttachment(newEntity.attachment);
        }
        if (!Objects.equals(newEntity.channel_id, this.channel_id)) {
            this.setChannel_id(newEntity.channel_id);
        }
    }
}
