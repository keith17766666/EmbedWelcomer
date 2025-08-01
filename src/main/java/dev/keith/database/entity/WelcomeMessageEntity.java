package dev.keith.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "welcome_message")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WelcomeMessageEntity {
    @Id
    private long server_id;
    @Column(name = "welcome_channel_id")
    private long channel_id;
    @Column(name = "welcome_message")
    private String message;

    public void modify(WelcomeMessageEntity newEntity) {

        if (!Objects.equals(newEntity.message, this.message)) {
            this.setMessage(newEntity.message);
        }
        if (!Objects.equals(newEntity.channel_id, this.channel_id)) {
            this.setChannel_id(newEntity.channel_id);
        }
    }
}
