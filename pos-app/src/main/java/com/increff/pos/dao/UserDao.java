package com.increff.pos.dao;

import com.increff.pos.pojo.UserPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class UserDao extends AbstractDao<UserPojo> {

    public UserPojo findByEmail(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserPojo> criteriaQuery = criteriaBuilder.createQuery(classType);
        Root<UserPojo> root = criteriaQuery.from(classType);

        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("email"), email));

        return getSingle(entityManager.createQuery(criteriaQuery));
    }

}
