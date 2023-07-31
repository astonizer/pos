package com.increff.pos.dto;

import com.google.gson.Gson;
import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ApiTestUtil apiTestUtil;

    // Tests if normalization works properly by seeing Case and trailing spaces
    @Test
    public void testBrandAdd1() throws ApiException {
        String brand = "    brAnD1  ", category = " CategOry1   ";
        BrandForm brandForm = apiTestUtil.getBrandForm(brand, category);
        BrandData brandData = brandDto.add(brandForm);

        BrandPojo brandPojo = brandDao.get(brandData.getId());
        assertEquals(brandPojo.getBrand(), "brand1");
        assertEquals(brandPojo.getCategory(), "category1");
    }

    // Tests if brand list is received correct
    @Test
    public void testBrandAdd2() throws ApiException {
        List<String> brandList = IntStream.range(0, 5).mapToObj(id -> ("   brAnD" + id)).collect(Collectors.toList());
        List<String> categoryList = IntStream.range(0, 5).mapToObj(id -> ("  CateGory" + id)).collect(Collectors.toList());
        List<BrandForm> brandFormList = apiTestUtil.getBrandFormList(brandList, categoryList);

        brandDto.addList(brandFormList);
        List<BrandPojo> brandPojoList = brandDao.getAll();
        IntStream.range(0, 5).forEach(id -> {
            assertEquals("brand"+id, brandPojoList.get(id).getBrand());
            assertEquals("category"+id, brandPojoList.get(id).getCategory());
        });
    }

    // Tests duplicated checking of brand list
    @Test
    public void testBrandAdd3() {
        List<String> brandList = IntStream.range(0, 5).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList = IntStream.range(0, 5).mapToObj(id -> ("category" + id)).collect(Collectors.toList());
        brandList.add("brand1");
        categoryList.add("category1");
        List<BrandForm> brandFormList = apiTestUtil.getBrandFormList(brandList, categoryList);

        try {
            brandDto.addList(brandFormList);
            fail();
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("Duplicate entry: brand1 - category1", errorList.get(5));
        }
    }

    // Tests adding incorrect form data
    @Test
    public void testBrandAdd4() {
        BrandForm brandForm = apiTestUtil.getBrandForm(null, "ff");

        try {
            brandDto.add(brandForm);
            fail();
        } catch (ApiException e) {
            assertEquals("brand: Null brand name;", e.getMessage());
        }
    }

    // Tests adding incorrect form data list
    @Test
    public void testBrandAdd5() {
        List<String> brandList = Arrays.asList(null, "brand");
        List<String> categoryList = Arrays.asList("category", null);
        List<BrandForm> brandFormList = apiTestUtil.getBrandFormList(brandList, categoryList);

        try {
            brandDto.addList(brandFormList);
            fail();
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("brand: Null brand name;", errorList.get(0));
            assertEquals("category: Null category name;", errorList.get(1));
        }
    }

    // Tests getting non-existent brand
    @Test
    public void testBrandGet1() {
        try {
            brandDto.getCheck(100);
            fail();
        } catch (ApiException e) {
            assertEquals("Brand doesn't exist", e.getMessage());
        }
    }

    // Tests getting brand
    @Test
    public void testBrandGet2() throws ApiException {
        String brand = "    brAnD1  ", category = " CategOry1   ";
        BrandForm brandForm = apiTestUtil.getBrandForm(brand, category);
        BrandData brandData = brandDto.add(brandForm);

        BrandData brandData1 = brandDto.getCheck(brandData.getId());
        assertEquals("brand1", brandData1.getBrand());
        assertEquals("category1", brandData1.getCategory());
    }

    // Tests getting paginated list
    @Test
    public void testBrandGet3() throws ApiException {
        List<String> brandList = IntStream.range(0, 5).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList = IntStream.range(0, 5).mapToObj(id -> ("category" + id)).collect(Collectors.toList());
        List<BrandPojo> brandPojoList = apiTestUtil.addBrandList(brandList, categoryList);

        Integer draw = 1, recordsFiltered = 5, recordsTotal = 5;
        PaginationData<BrandData> paginationData = brandDto.getPaginatedList(1, 2, 2);

        assertEquals(draw, paginationData.getDraw());
        assertEquals(recordsFiltered, paginationData.getRecordsFiltered());
        assertEquals(recordsTotal, paginationData.getRecordsTotal());
        IntStream.range(2, 4).forEach(id -> {
            BrandData brandData = paginationData.getData().get(id-2);
            assertEquals("brand" + id, brandData.getBrand());
            assertEquals("category" + id, brandData.getCategory());
        });
    }

    // Tests updating brand
    @Test
    public void testBrandUpdate1() throws ApiException {
        String brand1 = "brand1", brand2 = "brand2",
                category1 = "category1", category2 = "category2";
        Integer brandId = apiTestUtil.addBrand(brand1, category1).getId();
        BrandForm brandForm = apiTestUtil.getBrandForm(brand2, category2);

        brandDto.update(brandId, brandForm);
        BrandPojo brandPojo = brandDao.getAll().get(0);
        assertEquals("brand2", brandPojo.getBrand());
        assertEquals("category2", brandPojo.getCategory());
    }

}
