package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;
import org.hibernate.Session;
import org.hibernate.bugs.hhh18647.DemoEntity;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaSubQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;


public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @After
    public void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void hhh18647Test() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var cb = (HibernateCriteriaBuilder) entityManager.getCriteriaBuilder();
        var insertCriteria = cb.createCriteriaInsertValues(DemoEntity.class);
        insertCriteria.setInsertionTargetPaths(
                insertCriteria.getTarget().get("id"),
                // Here we'd like to insert into foreign key columns.
                insertCriteria.getTarget().get("a").get("id"), // a_id
                insertCriteria.getTarget().get("b").get("id"), // b_id
                insertCriteria.getTarget().get("c").get("id")  // c_id
        );
        insertCriteria.values(cb.values(
                cb.value(UUID.fromString("6a7078ef-d761-4e05-b743-0d4b9eb242cf")),
                cb.value(1),
                cb.value(2),
                cb.value(3)
        ));
        entityManager.unwrap(Session.class).createMutationQuery(insertCriteria)
                .executeUpdate(); // Not expecting multiple table references for an SQM INSERT-SELECT exception :(

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    public void hhh17776Test() throws Exception {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Assertions.assertDoesNotThrow(() -> getEntities(entityManager));
        var actual = getEntities(entityManager);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals("qRf5GhP2n9sT3dKm", actual.get(0).getConsumerId());
        Assertions.assertEquals("J7kP4mN9qRtA2bSd", actual.get(0).getSupplierId());
        Assertions.assertEquals("eF6hT8wZ5yX1vD3u", actual.get(1).getConsumerId());
        Assertions.assertEquals("gH5sR2fT9qW7eY4d", actual.get(1).getSupplierId());

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private List<OrderEntity> getEntities(EntityManager entityManager) {

        var builder = entityManager.unwrap(Session.class).getCriteriaBuilder();
        var mainQuery = builder.createQuery(OrderEntity.class);
        var unionAllSubQuery = buildUnionAllSubquery(builder, mainQuery);
        var mainQueryRoot = mainQuery.from(unionAllSubQuery);

        mainQuery.multiselect(
                mainQueryRoot.get("consumerId").alias("consumerId"),
                mainQueryRoot.get("supplierId").alias("supplierId")
        );

        return entityManager.createQuery(mainQuery)
                .getResultList();
    }

    private JpaSubQuery<Tuple> buildUnionAllSubquery(HibernateCriteriaBuilder builder,
                                                     JpaCriteriaQuery<OrderEntity> unionAllQuery) {

        JpaSubQuery<Tuple> q1 = unionAllQuery.subquery(Tuple.class);
        JpaSubQuery<Tuple> q2 = unionAllQuery.subquery(Tuple.class);

        q1.multiselect(
                builder.literal("qRf5GhP2n9sT3dKm").alias("consumerId"),
                builder.literal("J7kP4mN9qRtA2bSd").alias("supplierId")
        );

        q2.multiselect(
                builder.literal("eF6hT8wZ5yX1vD3u").alias("consumerId"),
                builder.literal("gH5sR2fT9qW7eY4d").alias("supplierId")
        );

        // This is a workaround to make it work.
//        ((SqmSubQuery) q1).getQuerySpec().setFromClause(new SqmFromClause());
//        ((SqmSubQuery) q2).getQuerySpec().setFromClause(new SqmFromClause());

        return builder.unionAll(
                q1,
                q2
        );
    }
}
