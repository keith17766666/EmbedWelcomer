package dev.keith.database.helpers;

import dev.keith.database.entity.LeaveEmbedMessageEntity;
import dev.keith.database.entity.LeaveMessageEntity;
import dev.keith.database.entity.WelcomeEmbedMessageEntity;
import dev.keith.database.entity.WelcomeMessageEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public abstract class SQLHelper<E> {
    protected static final SessionFactory sessionFactory = setUp();

    private static SessionFactory setUp() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .loadProperties(new File(System.getProperty("user.dir") +
                                "/hibernate.properties"))
                        .build();
        try {
            return new MetadataSources(registry)
                            .addAnnotatedClass(WelcomeMessageEntity.class)
                            .addAnnotatedClass(LeaveMessageEntity.class)
                            .addAnnotatedClass(WelcomeEmbedMessageEntity.class)
                            .addAnnotatedClass(LeaveEmbedMessageEntity.class)
                            .buildMetadata()
                            .buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw new RuntimeException("Error when initialing the session factory", e);
        }
    }

    static {
        try {
            Class.forName("java.sql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public abstract void insert(E entity);
    public abstract void modify(long id, E entity);
    public abstract void delete(long id);
    public abstract E query(long id);
    public void storeIfAbsent(long id, E entity) {
        if (query(id) == null) {
            insert(entity);
        } else {
            modify(id, entity);
        }
    }

    @Override
    @SuppressWarnings("removal")
    protected void finalize() {
        sessionFactory.close();
    };

    protected void inTransaction(BiConsumer<Session, Transaction> action) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();
        action.accept(session, transaction);
        transaction.commit();
    }
    protected <R> R inTransaction(BiFunction<Session, Transaction, R> action) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();
        R r = action.apply(session, transaction);
        transaction.commit();
        return r;
    }
    @SuppressWarnings({"unchecked", "deprecation"})
    protected List<E> entries(String query, Session session) {
        return (List<E>) session.createQuery(query).getResultList();
    }
    protected Optional<E> findFirst(List<E> list, Predicate<? super E> predicate) {
        return list.stream()
                .filter(predicate)
                .findFirst();
    }
}
