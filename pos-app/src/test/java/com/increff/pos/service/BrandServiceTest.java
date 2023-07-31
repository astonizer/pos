package com.increff.pos.service;

import com.google.gson.Gson;
import com.increff.pos.dao.BrandDao;
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
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class BrandServiceTest extends AbstractUnitTest {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ApiTestUtil apiTestUtil;

    // Tests adding brand with brand/category combination already existing
    @Test
    public void testAdd1() {
        String brand = "brand";
        String category = "category";
        BrandPojo existingBrand = apiTestUtil.addBrand(brand, category);

        // Attempt to add the same brand again
        try {
            brandService.add(existingBrand);
            fail("Adding existing brand did not throw an exception");
        } catch(ApiException e) {
            assertEquals("Brand '" + brand + "' with category '" + category + "' already exists", e.getMessage());
        }
    }

    // Tests adding new brand
    @Test
    public void testAdd2() throws ApiException {
        String brand = "brand", category = "category";
        BrandPojo brandPojo = apiTestUtil.createBrand(brand, category);

        brandService.add(brandPojo);

        List<BrandPojo> retrievedBrandList = brandDao.getAll();
        assertEquals(1, retrievedBrandList.size());
        assertEquals(brandPojo, retrievedBrandList.get(0));
    }

    // Tests adding brand list
    @Test
    public void testAdd3() throws ApiException {
        List<String> brandList = IntStream.range(1, 4).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList = IntStream.range(1, 4).mapToObj(id -> ("category" + id)).collect(Collectors.toList());
        List<BrandPojo> brandPojoList = apiTestUtil.createBrandList(brandList, categoryList);

        brandService.addList(brandPojoList);

        List<BrandPojo> retrievedBrandList = brandDao.getAll();
        assertEquals(brandPojoList.size(), retrievedBrandList.size());
        for (BrandPojo brandPojo : brandPojoList) {
            assertTrue(retrievedBrandList.contains(brandPojo));
        }
    }

    // Tests adding brand list with errors
    @Test
    public void testAdd4() {
        List<String> brandList1 = IntStream.range(1, 4).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList1 = IntStream.range(1, 4).mapToObj(id -> ("category" + id)).collect(Collectors.toList());

        List<BrandPojo> brandPojoList1 = apiTestUtil.addBrandList(brandList1, categoryList1);

        List<String> brandList2 = IntStream.range(3, 6).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList2 = IntStream.range(3, 6).mapToObj(id -> ("category" + id)).collect(Collectors.toList());

        List<BrandPojo> brandPojoList2 = apiTestUtil.createBrandList(brandList2, categoryList2);

        try {
            brandService.addList(brandPojoList2);
            fail("Adding brand list with duplicate entries did not throw an exception");
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("Duplicate entry: brand3 - category3", errorList.get(0));
        }
    }

    // Tests getting non-existent brand
    @Test
    public void testGet1() {
        Integer id = 100;
        try {
            brandService.getCheck(id);
            fail("Non existing brand was fetched");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Brand doesn't exist");
        }
    }

    // Tests getting existing brand by id
    @Test
    public void testGet2() throws ApiException {
        String brand = "brand";
        String category = "category";
        BrandPojo existingBrand = apiTestUtil.addBrand(brand, category);

        BrandPojo brandPojo = brandService.getCheck(existingBrand.getId());
        assertEquals(existingBrand, brandPojo);
    }

    // Tests getting existing brand by brand and category
    @Test
    public void testGet3() throws ApiException {
        String brand = "brand";
        String category = "category";
        BrandPojo existingBrand = apiTestUtil.addBrand(brand, category);

        BrandPojo brandPojo = brandService.getCheck("brand", "category");
        assertEquals(existingBrand, brandPojo);
    }

    // Tests getting all brands
    @Test
    public void testGet4() {
        List<String> brandList1 = IntStream.range(1, 4).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList1 = IntStream.range(1, 4).mapToObj(id -> ("category" + id)).collect(Collectors.toList());
        List<BrandPojo> brandPojoList1 = apiTestUtil.addBrandList(brandList1, categoryList1);

        List<BrandPojo> brandPojoList2 = brandService.getAll();
        assertEquals(brandPojoList1, brandPojoList2);
    }

    // Tests getting paginated list
    @Test
    public void testGet5() {
        List<String> brandList1 = IntStream.range(1, 6).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList1 = IntStream.range(1, 6).mapToObj(id -> ("category" + id)).collect(Collectors.toList());
        List<BrandPojo> brandPojoList1 = apiTestUtil.addBrandList(brandList1, categoryList1);

        List<BrandPojo> brandPojoList2 = brandService.getPaginatedList(2, 3);
        assertEquals(3, brandPojoList2.size());
        assertEquals(brandPojoList1.get(2), brandPojoList2.get(0));
        assertEquals(brandPojoList1.get(3), brandPojoList2.get(1));
        assertEquals(brandPojoList1.get(4), brandPojoList2.get(2));
    }

    // Tests getting brand count
    @Test
    public void testGet6() {
        Integer count = 9;
        List<String> brandList1 = IntStream.range(1, count+1).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList1 = IntStream.range(1, count+1).mapToObj(id -> ("category" + id)).collect(Collectors.toList());
        List<BrandPojo> brandPojoList1 = apiTestUtil.addBrandList(brandList1, categoryList1);

        Integer brandCount = brandService.getCount();
        assertEquals(count, brandCount);
    }

    // Tests getting all brands with brand/category
    @Test
    public void testGet7() throws ApiException {
        List<String> brandList1 = Arrays.asList("brand1", "brand2");
        List<String> categoryList1 = Arrays.asList("category1", "category2");
        List<BrandPojo> brandPojoList1 = apiTestUtil.addBrandList(brandList1, categoryList1);

        List<BrandPojo> brandPojoList2 = brandService.getAll("brand1", "category1");
        assertEquals(1, brandPojoList2.size());
        assertEquals(brandPojoList1.get(0), brandPojoList2.get(0));
    }

    // Tests getting all brands with empty brand/category
    @Test
    public void testGet8() throws ApiException {
        List<String> brandList1 = Arrays.asList("brand1", "brand2");
        List<String> categoryList1 = Arrays.asList("category1", "category2");
        List<BrandPojo> brandPojoList1 = apiTestUtil.addBrandList(brandList1, categoryList1);

        List<BrandPojo> brandPojoList2 = brandService.getAll("", null);
        assertEquals(2, brandPojoList2.size());
        IntStream.range(0, 2).forEach(id -> {
            assertEquals(brandPojoList1.get(id), brandPojoList2.get(id));
        });
    }

    // Tests updating non-existent brand
    @Test
    public void testUpdate1() {
        String brand = "brand", category = "category";
        Integer id = 100;

        try {
            brandService.update(id, brand ,category);
            fail("Updating non existent brand worked");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Brand doesn't exist");
        }
    }

    // Tests updating brand with existing brand category
    @Test
    public void testUpdate2() {
        BrandPojo brandPojo = apiTestUtil.addBrand("brand", "category");
        BrandPojo brandPojo1 = apiTestUtil.addBrand("brand1", "category1");

        try {
            brandService.update(brandPojo1.getId(), "brand", "category");
            fail("Updated brand with existing brand-category values");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Brand 'brand' with category 'category' already exists");
        }
    }

    // Tests updating correctly
    @Test
    public void testUpdate3() throws ApiException {
        BrandPojo brandPojo = apiTestUtil.addBrand("brand", "category");

        BrandPojo brandPojo1 = brandService.update(brandPojo.getId(), "brand1", "category1");
        assertEquals(brandPojo1.getId(), brandPojo.getId());
        assertEquals(brandPojo1.getCategory(), "category1");
        assertEquals(brandPojo1.getBrand(), "brand1");
    }

}
