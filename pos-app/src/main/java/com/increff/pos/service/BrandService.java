package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.ValidateUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrandService extends AbstractService {

    @Autowired
    private BrandDao brandDao;

    public BrandPojo add(BrandPojo brandPojo) throws ApiException {
        checkIfBrandAndCategoryExists(brandPojo.getBrand(), brandPojo.getCategory());

        return brandDao.save(brandPojo);
    }

    public List<BrandPojo> addList(List<BrandPojo> brandPojoList) throws ApiException {
        validateList(brandPojoList);

        return brandPojoList.stream()
                .map(brandDao::save)
                .collect(Collectors.toList());
    }

    public BrandPojo getCheck(Integer id) throws ApiException {
        BrandPojo brandPojo = brandDao.get(id);

        checkNotNull(brandPojo, "Brand doesn't exist");

        return brandPojo;
    }

    public BrandPojo getCheck(String brand, String category) throws ApiException {
        BrandPojo brandPojo = brandDao.getByBrandAndCategory(brand, category);

        checkNotNull(brandPojo, "Brand " + brand + " - " + category + " doesn't exist");

        return brandPojo;
    }

    public List<BrandPojo> getAll(String brand, String category) throws ApiException {
        if((brand == null || brand.isEmpty()) &&
                (category == null || category.isEmpty())) {
            return getAll();
        }

        return brandDao.getListByBrandAndCategory(brand, category);
    }

    public List<BrandPojo> getAll() {
        return brandDao.getAll();
    }

    public List<BrandPojo> getPaginatedList(Integer start, Integer length) {
        return brandDao.getAllByPage(start, length);
    }

    public Integer getCount() {
        return brandDao.getCount();
    }

    public BrandPojo update(Integer id, String brand, String category) throws ApiException {
        BrandPojo brandPojo = getCheck(id);

        // case when values are changed
        if ((!brandPojo.getBrand().equals(brand)) || (!brandPojo.getCategory().equals(category))) {
            checkIfBrandAndCategoryExists(brand, category);
            brandPojo.setBrand(brand);
            brandPojo.setCategory(category);
        }

        return brandPojo;
    }

    public List<BrandPojo> getByBrandListAndCategoryList(List<BrandPojo> brandPojoList) {
        List<String> brandList = brandPojoList.stream().map(BrandPojo::getBrand).collect(Collectors.toList());
        List<String> categoryList = brandPojoList.stream().map(BrandPojo::getCategory).collect(Collectors.toList());

        return brandDao.getListByBrandListAndCategoryList(brandList, categoryList);
    }

    private void validateList(List<BrandPojo> brandPojoList) throws ApiException {
        Set<Pair<String, String>> brandPojoSet = getSetOfExistingBrands(brandPojoList);

        ValidateUtil.validateList(brandPojoList, brandPojoSet, (brand, brandSet) -> {
            Pair<String, String> brandCategoryPair = new Pair<>(brand.getBrand(), brand.getCategory());
            if (!brandSet.add(brandCategoryPair)) {
                return new Pair<>(true, "Duplicate entry: " + brandCategoryPair.getKey() + " - " + brandCategoryPair.getValue());
            }
            return new Pair<>(false, null);
        });
    }

    private Set<Pair<String, String>> getSetOfExistingBrands(List<BrandPojo> brandPojoList) {
        List<BrandPojo> filteredBrandPojoList = getByBrandListAndCategoryList(brandPojoList);

        return filteredBrandPojoList.stream()
                .map(brandPojo -> new Pair<>(brandPojo.getBrand(), brandPojo.getCategory()))
                .collect(Collectors.toSet());
    }

    private void checkIfBrandAndCategoryExists(String brand, String category) throws ApiException {
        BrandPojo brandPojo = brandDao.getByBrandAndCategory(brand, category);

        checkNull(brandPojo, "Brand '" + brand + "' with category '" + category + "' already exists");
    }

}
