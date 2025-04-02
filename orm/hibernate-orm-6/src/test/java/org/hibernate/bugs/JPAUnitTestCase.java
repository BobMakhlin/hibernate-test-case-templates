package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @AfterEach
    void destroy() {
        entityManagerFactory.close();
    }

    /*
    insert into ItemEntity(id)
    values('5a033489-7390-46b1-9752-4c9836816ee7')
    on conflict do nothing
     */
    @Test
    void hhh19314_createCriteriaInsertValues_test() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var cb = (HibernateCriteriaBuilder) entityManager.getCriteriaBuilder();
        var insertIntoItem = cb.createCriteriaInsertValues(ItemEntity.class);
        insertIntoItem.setInsertionTargetPaths(insertIntoItem.getTarget().get("id"));
        insertIntoItem.values(cb.values(cb.value(UUID.randomUUID())));
        insertIntoItem.onConflict().onConflictDoNothing();

        Assertions.assertDoesNotThrow(() -> entityManager.unwrap(Session.class).createMutationQuery(insertIntoItem).executeUpdate());  // StackOverflowError

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /*
    insert into ItemEntity(id)
    select '5a033489-7390-46b1-9752-4c9836816ee7'
    on conflict do nothing
     */
    @Test
    void hhh19314_createCriteriaInsertSelect_test() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var cb = (HibernateCriteriaBuilder) entityManager.getCriteriaBuilder();
        var insertIntoItem = cb.createCriteriaInsertSelect(ItemEntity.class);
        insertIntoItem.setInsertionTargetPaths(insertIntoItem.getTarget().get("id"));
        var cq = cb.createQuery(Tuple.class);
        cq.multiselect(cb.literal(UUID.randomUUID()));
        insertIntoItem.select(cq);
        insertIntoItem.onConflict().onConflictDoNothing();

        Assertions.assertDoesNotThrow(() -> entityManager.unwrap(Session.class).createMutationQuery(insertIntoItem).executeUpdate());  // StackOverflowError

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
