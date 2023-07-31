package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
@Log4j
public class ProductDao extends AbstractDao<ProductPojo> {

    public <T> List<ProductPojo> getAllByColumList(List<T> tList, String column) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductPojo> criteriaQuery = criteriaBuilder.createQuery(classType);
        Root<ProductPojo> root = criteriaQuery.from(classType);

        criteriaQuery.select(root).where(root.get(column).in(tList));

        List<ProductPojo> p =  entityManager.createQuery(criteriaQuery).getResultList();
        return p;
    }
    
}
