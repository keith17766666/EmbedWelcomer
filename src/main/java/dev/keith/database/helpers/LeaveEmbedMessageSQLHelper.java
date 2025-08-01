package dev.keith.database.helpers;

import dev.keith.database.entity.LeaveEmbedMessageEntity;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class LeaveEmbedMessageSQLHelper extends SQLHelper<LeaveEmbedMessageEntity> {
    @Override
    public void insert(LeaveEmbedMessageEntity entity) {
        inTransaction((session, transaction) -> {
            session.persist(entity);
        });
    }
    @Override
    public void modify(long serverId, LeaveEmbedMessageEntity entity) {
        inTransaction((session, transaction) -> {
            List<LeaveEmbedMessageEntity> messageEntities =
                    entries("from LeaveEmbedMessageEntity", session);
            
            Optional<LeaveEmbedMessageEntity> entityTargeted = findFirst(messageEntities, 
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
            List<LeaveEmbedMessageEntity> messageEntities =
                    entries("from LeaveEmbedMessageEntity", session);
            Optional<LeaveEmbedMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            if (entityTargeted.isEmpty()) {
                return;
            }
            session.remove(entityTargeted.get());
        });
    }
    @Override
    public LeaveEmbedMessageEntity query(long serverId) {
        return inTransaction((session, transaction) -> {
            List<LeaveEmbedMessageEntity> messageEntities =
                    entries("from LeaveEmbedMessageEntity", session);
            Optional<LeaveEmbedMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            return entityTargeted.orElse(null);
        });
    }
}
