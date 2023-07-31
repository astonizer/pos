package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository
public class OrderItemDao extends AbstractDao<OrderItemPojo> {

    public OrderItemPojo getByOrderIdAndProductId(Integer orderId, Integer productId) {
        CriteriaQuery<OrderItemPojo> query = criteriaBuilder.createQuery(OrderItemPojo.class);
        Root<OrderItemPojo> root = query.from(OrderItemPojo.class);

        query.where(criteriaBuilder.and(
                criteriaBuilder.equal(root.get("orderId"), orderId),
                criteriaBuilder.equal(root.get("productId"), productId)
        ));

        return getSingle(entityManager.createQuery(query));
    }

}
