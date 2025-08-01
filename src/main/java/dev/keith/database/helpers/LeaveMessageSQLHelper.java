package dev.keith.database.helpers;

import dev.keith.database.entity.LeaveMessageEntity;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class LeaveMessageSQLHelper extends SQLHelper<LeaveMessageEntity> {
    @Override
    public void insert(LeaveMessageEntity entity) {
        inTransaction((session, transaction) -> {
            session.persist(entity);
        });
    }
    @Override
    public void modify(long serverId, LeaveMessageEntity entity) {
        inTransaction((session, transaction) -> {
            List<LeaveMessageEntity> messageEntities =
                    entries("from LeaveMessageEntity", session);
            
            Optional<LeaveMessageEntity> entityTargeted = findFirst(messageEntities, 
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
            List<LeaveMessageEntity> messageEntities =
                    entries("from LeaveMessageEntity", session);
            Optional<LeaveMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            if (entityTargeted.isEmpty()) {
                return;
            }
            session.remove(entityTargeted.get());
        });
    }
    @Override
    public LeaveMessageEntity query(long serverId) {
        return inTransaction((session, transaction) -> {
            List<LeaveMessageEntity> messageEntities =
                    entries("from LeaveMessageEntity", session);
            Optional<LeaveMessageEntity> entityTargeted =
                    findFirst(messageEntities, e -> e.getServer_id() == serverId);
            return entityTargeted.orElse(null);
        });
    }
}
