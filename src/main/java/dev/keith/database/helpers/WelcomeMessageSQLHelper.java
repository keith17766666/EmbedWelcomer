package dev.keith.database.helpers;

import dev.keith.database.entity.WelcomeMessageEntity;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class WelcomeMessageSQLHelper extends SQLHelper<WelcomeMessageEntity> {
    @Override
    public void insert(WelcomeMessageEntity entity) {
        inTransaction((session, transaction) -> {
            session.persist(entity);
        });
    }
    @Override
    public void modify(long serverId, WelcomeMessageEntity entity) {
        inTransaction((session, transaction) -> {
            List<WelcomeMessageEntity> messageEntities =
                    entries("from WelcomeMessageEntity", session);

            Optional<WelcomeMessageEntity> entityTargeted = findFirst(messageEntities,
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
            List<WelcomeMessageEntity> messageEntities =
                    entries("from WelcomeMessageEntity", session);
            Optional<WelcomeMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            if (entityTargeted.isEmpty()) {
                return;
            }
            session.remove(entityTargeted.get());
        });
    }
    @Override
    public WelcomeMessageEntity query(long serverId) {
        return inTransaction((session, transaction) -> {
            List<WelcomeMessageEntity> messageEntities =
                    entries("from WelcomeMessageEntity", session);
            Optional<WelcomeMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            return entityTargeted.orElse(null);
        });
    }
}
