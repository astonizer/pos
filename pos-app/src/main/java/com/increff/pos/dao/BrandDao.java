package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
@Log4j
public class BrandDao extends AbstractDao<BrandPojo> {

    public BrandPojo getByBrandAndCategory(String brand, String category) {
        return getSingle(buildFilteredQuery(brand, category));
    }

    public List<BrandPojo> getListByBrandAndCategory(String brand, String category) {
        return buildFilteredQuery(brand, category).getResultList();
    }

    public List<BrandPojo> getListByBrandListAndCategoryList(List<String> brandList, List<String> categoryList) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BrandPojo> criteriaQuery = criteriaBuilder.createQuery(classType);
        Root<BrandPojo> root = criteriaQuery.from(classType);

        Predicate predicate = criteriaBuilder.conjunction();

        if(!brandList.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("brand").in(brandList));
        }

        if(!categoryList.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, root.get("category").in(categoryList));
        }

        criteriaQuery.select(root).where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private Query buildFilteredQuery(String brand, String category) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BrandPojo> query = criteriaBuilder.createQuery(classType);
        Root<BrandPojo> root = query.from(classType);
        Predicate predicate = criteriaBuilder.conjunction();
        if (brand != null && !brand.isEmpty()) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(root.get("brand"), brand)
            );
        }
        if (category != null && !category.isEmpty()) {
            predicate = criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.equal(root.get("category"), category)
            );
        }
        query.select(root).where(predicate);
        return entityManager.createQuery(query);
    }

}
