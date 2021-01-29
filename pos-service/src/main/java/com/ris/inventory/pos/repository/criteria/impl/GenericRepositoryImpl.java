package com.ris.inventory.pos.repository.criteria.impl;

import com.google.gson.Gson;
import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.domain.Auditable;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.repository.GenericRepository;
import com.ris.inventory.pos.util.exception.EntityUpdateException;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.*;

public class GenericRepositoryImpl<T extends Auditable> implements GenericRepository<T> {

    private final static Logger log = LoggerFactory.getLogger(GenericRepositoryImpl.class);

    private Class<T> entityType;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private Map<String, Object> getDeleteQuery() {
        log.info("creating generic delete query");
        Map<String, Object> query = new HashMap<>();
        query.put("isDeleted", true);
        return query;
    }

    protected final Session getCurrentSession(Interceptor interceptor) {
        SessionBuilder sessionBuilder = entityManagerFactory.unwrap(SessionFactory.class).withOptions().jdbcTimeZone(TimeZone.getTimeZone("UTC"));

        AuditInterceptor auditInterceptor = (AuditInterceptor) interceptor;
        Session session = sessionBuilder.interceptor(auditInterceptor).openSession();
        auditInterceptor.setSession(session);
        return session;
    }

    protected final Session getCurrentSession() {
        return entityManagerFactory.unwrap(SessionFactory.class).withOptions().jdbcTimeZone(TimeZone.getTimeZone("UTC")).openSession();
    }

    @Override
    public void setEntity(Class<T> clazz) {
        this.entityType = clazz;
    }

    @Override
    public T get(Long id) {
        log.info("Generic Query for getting entity by id '{}'", id);
        return get("id", id);
    }

    @Override
    public List<T> findAllByColumn(String column, Object value) {
        log.info("Generic Query for getting all entities by column '{}' and value '{}'", column, value);
        return getAll(column, value);
    }

    @Override
    public List<T> findAllByColumn(Pagination pagination, String column, Object value) {
        log.info("Generic Query for all entities by column '{}' and value '{}' with limit and offset", column, value);

        List<T> entities = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            log.info("Getting list  of entities for '{}' with offset '{}' and limit '{}'", entityType.toString(), pagination, value);


            pagination.verify(countQuery(session, column, value).intValue());

            entities = list_(session, pagination.getOffset(), pagination.getLimit(), column, value);

        } catch (Exception ex) {
            log.error("Entities did not get due to exception");
            ex.printStackTrace();
        }
        return entities;
    }

    private Long countQuery(Session session, String column, Object value) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<T> root = criteria.from(entityType);
        criteria.select(builder.count(root)).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.equal(root.get(column), value)));
        return session.createQuery(criteria).getSingleResult();
    }

    @Override
    public T findByColumn(String column, Object value) {
        log.info("Generic Query for getting an entity by column '{}' and value '{}'", column, value);
        return get(column, value);
    }

    @Override
    public T save(T object, Interceptor interceptor) {
        log.info("Generic Query for persisting an entity for '{}'", object);

        T entity = null;
        Transaction transaction = null;
        try (Session session = getCurrentSession(interceptor)) {
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
            log.info("Entity is persisted successfully");
            entity = object;
        } catch (Exception ex) {
            log.error("Entity can not be persisted due to this exception");
            rollBackTransaction(transaction);
            ex.printStackTrace();
        }
        return entity;
    }

    @Override
    public void setDeleted(Long id) {
        log.info("Entity is going to set as deleted {}", id);

        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            session.createQuery(getUpdateCriteriaQuery(session, getDeleteQuery(), id));
            transaction.commit();
            log.info("Entity has been marked as deleted {}", id);
        } catch (Exception ex) {
            log.error("Entity can not be set as deleted due to exception");
            rollBackTransaction(transaction);
            ex.printStackTrace();
            throw new EntityUpdateException("Entity can not be set as deleted due to exception");
        }
    }

    @Override
    public void update(Map<String, Object> object, Long id) {
        log.info("Entity is going to update by by generic update for these columns {}", new Gson().toJson(object));

        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            session.createQuery(getUpdateCriteriaQuery(session, object, id)).executeUpdate();
            transaction.commit();
            log.info("Entity has been updated successfully by generic update");
        } catch (Exception ex) {
            log.error("Entity can not be set as deleted due to exception");
            rollBackTransaction(transaction);
            ex.printStackTrace();
            throw new EntityUpdateException("Entity can not be set as deleted due to exception");
        }
    }

    @Override
    public List<T> list() {
        List<T> entities = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            log.info("Getting list of entities for '{}'", entityType.toString());

            entities = produceListQuery(session).getResultList();
        } catch (Exception ex) {
            log.error("Entities did not get due to exception");
            ex.printStackTrace();
        }
        return entities;
    }

    //Descending sorting
    @Override
    public void sort(List<T> data, final String column) {
        data.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if ("dateCreated".equals(column)) {
                    return o1.getDateCreated().after(o2.getDateCreated()) ? -1 : 0;
                }
                return o1.getId() > o2.getId() ? -1 : 0;
            }
        });
    }

    @Override
    public List<T> list(int offset, int limit) {
        List<T> entities = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            log.info("Getting list  of entities for '{}' with offset '{}' and limit '{}'", entityType.toString(), offset, limit);

            entities = produceListQuery(session).setFirstResult(offset).setMaxResults(limit).getResultList();
        } catch (Exception ex) {
            log.error("Entities did not get due to exception");
            ex.printStackTrace();
        }
        return entities;
    }

    private List<T> list_(Session session, int offset, int limit, String column, Object value) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityType);
        Root<T> root = criteria.from(entityType);
        criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.equal(root.get(column), value)));
        return session.createQuery(criteria).setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Override
    public int count() {
        int count = 0;

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<T> root = criteria.from(entityType);
            criteria.select(builder.count(root.get("id"))).where(builder.isFalse(root.get("isDeleted")));
            count = session.createQuery(criteria).getSingleResult().intValue();
        } catch (Exception ex) {
            log.error("Error occurred while counting the records of this entity.");
            ex.printStackTrace();
        }
        return count;
    }

    private Query<T> produceListQuery(Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(entityType);
        Root<T> root = criteria.from(entityType);
        criteria.select(root).where(builder.isFalse(root.get("isDeleted")));
        return session.createQuery(criteria);
    }

    private T get(String column, Object value) {
        log.info("Generic Query for column {} and value {}", column, value);
        try (Session session = getCurrentSession()) {
            return session.createQuery(getCriteriaQuery(session, column, value)).getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Generic Query can not be completed for {} column due to this exception", column);
            return null;
        }
    }

    private List<T> getAll(String column, Object value) {
        log.info("Generic Query for column {} and value {}", column, value);
        try (Session session = getCurrentSession()) {
            return session.createQuery(getCriteriaQuery(session, column, value)).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Generic Query can not be completed for list output of {} column due to this exception", column);
            return null;
        }
    }

    protected CriteriaQuery<T> getCriteriaQuery(Session session, String column, Object value) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(entityType);
        Root<T> entityRoot = criteriaQuery.from(entityType);

        criteriaQuery.select(entityRoot).where(
                builder.and(builder.equal(entityRoot.get(column), value), builder.equal(entityRoot.get("isDeleted"), false)));

        log.info("search criteria query is created successfully");
        return criteriaQuery;
    }

    private CriteriaUpdate<T> getUpdateCriteriaQuery(Session session, Map<String, Object> objectMap, Long id) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = builder.createCriteriaUpdate(entityType);
        Root<T> entityRoot = criteriaUpdate.from(entityType);

        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            criteriaUpdate.set(entry.getKey(), entry.getValue());
        }

        criteriaUpdate.where(builder.equal(entityRoot.get("id"), id));

        log.info("update criteria query is created successfully with id where clause");
        return criteriaUpdate;
    }

    protected void rollBackTransaction(Transaction transaction) {
        if (transaction != null && (transaction.getStatus() == TransactionStatus.ACTIVE || transaction.getStatus() == TransactionStatus.MARKED_ROLLBACK))
            transaction.rollback();

        log.info("transaction is rolled back successfully.");
    }
}
