package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {

    public List<InventoryPojo> getByIdList(List<Integer> idList) {
        CriteriaQuery<InventoryPojo> criteriaQuery = criteriaBuilder.createQuery(classType);
        Root<InventoryPojo> root = criteriaQuery.from(classType);

        Predicate predicate = root.get("productId").in(idList);
        criteriaQuery.select(root).where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
