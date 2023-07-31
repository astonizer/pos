package com.increff.pos.dao;

import lombok.extern.log4j.Log4j;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Log4j
public abstract class AbstractDao<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    protected CriteriaBuilder criteriaBuilder;

    protected Class<T> classType;

    public AbstractDao() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();

        this.classType = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @PostConstruct
    private void init() {
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public T save(T entity) {
        entityManager.persist(entity);

        return entity;
    }

    public T get(Integer id) {
        return entityManager.find(classType, id);
    }

    public List<T> getAll() {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(classType);
        Root<T> root = criteriaQuery.from(classType);
        criteriaQuery.select(root);

        return entityManager
                .createQuery(criteriaQuery)
                .getResultList();
    }

    public List<T> getAllByPage(Integer start, Integer length) {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(classType);
        Root<T> root = criteriaQuery.from(classType);
        criteriaQuery.select(root);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(start)
                .setMaxResults(length)
                .getResultList();
    }

    public <T> Integer getCount() {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = (Root<T>) criteriaQuery.from(classType);
        criteriaQuery.select(criteriaBuilder.count(root));
        Long count = getSingle(entityManager.createQuery(criteriaQuery));
        return Math.toIntExact(count);
    }

    public <V, P> P getBySingleColumn(String columnName, V value) {
        return getSingle(createSingleColumnQuery(columnName, value, classType));
    }

    public <V, P> List<P> getAllBySingleColumn(String columnName, V value) {
        return createSingleColumnQuery(columnName, value, classType).getResultList();
    }

    public <V, P> void deleteByColumn(String columnName, V value, Class<P> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<P> deleteQuery = criteriaBuilder.createCriteriaDelete(entityClass);
        Root<P> root = deleteQuery.from(entityClass);
        deleteQuery.where(criteriaBuilder.equal(root.get(columnName), value));

        entityManager
                .createQuery(deleteQuery)
                .executeUpdate();
    }

    protected <P> P getSingle(Query query) {
        return (P) query.getResultList().stream().findFirst().orElse(null);
    }

    protected <V, P> Query createSingleColumnQuery(String columnName, V value, Class<P> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<P> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<P> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get(columnName), value));

        return entityManager.createQuery(criteriaQuery);
    }

}
