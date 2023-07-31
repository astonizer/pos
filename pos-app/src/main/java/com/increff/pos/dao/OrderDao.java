package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.model.enums.OrderStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao<OrderPojo> {


    public List<OrderPojo> findInvoicedOrdersInRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderPojo> query = cb.createQuery(classType);
        Root<OrderPojo> root = query.from(classType);

        Predicate statusPredicate = cb.equal(root.get("status"), OrderStatus.INVOICED);
        Predicate timePredicate = cb.between(root.get("updatedAt"), startDate, endDate);

        query.select(root).where(cb.and(statusPredicate, timePredicate));

        return entityManager.createQuery(query).getResultList();
    }

}
