package dev.keith.database.helpers;

import dev.keith.database.entity.WelcomeEmbedMessageEntity;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class WelcomeEmbedMessageSQLHelper extends SQLHelper<WelcomeEmbedMessageEntity> {
    @Override
    public void insert(WelcomeEmbedMessageEntity entity) {
        inTransaction((session, transaction) -> {
            session.persist(entity);
        });
    }
    @Override
    public void modify(long serverId, WelcomeEmbedMessageEntity entity) {
        inTransaction((session, transaction) -> {
            List<WelcomeEmbedMessageEntity> messageEntities =
                    entries("from WelcomeEmbedMessageEntity", session);
            
            Optional<WelcomeEmbedMessageEntity> entityTargeted = findFirst(messageEntities, 
                    e -> e.getServer_id() == serverId);
            if (entityTargeted.isEmpty()) {
                return;
            }
            entityTargeted.get().modify(entity);
        });
    }
    @Override
    public void delete(long serverId) {
        inTransaction((session, transaction) -> {
            List<WelcomeEmbedMessageEntity> messageEntities =
                    entries("from WelcomeEmbedMessageEntity", session);
            Optional<WelcomeEmbedMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            if (entityTargeted.isEmpty()) {
                return;
            }
            session.remove(entityTargeted.get());
        });
    }
    @Override
    public WelcomeEmbedMessageEntity query(long serverId) {
        return inTransaction((session, transaction) -> {
            List<WelcomeEmbedMessageEntity> messageEntities =
                    entries("from WelcomeEmbedMessageEntity", session);
            Optional<WelcomeEmbedMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            return entityTargeted.orElse(null);
        });
    }
}
