package com.increff.pos.service;

import com.google.gson.Gson;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class ProductServiceTest extends AbstractUnitTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ApiTestUtil apiTestUtil;

    // Tests adding product with same barcode, must throw exception
    @Test
    public void testProductAdd1() {
        String barcode1 = "barcode1", name1 = "name1";
        Double mrp1 = 100.15;
        ProductPojo productPojo = apiTestUtil.addProduct(barcode1, 1, name1, mrp1);
        try {
            productService.add(productPojo, 1);
            fail("Adding duplicate product worked");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Product with barcode 'barcode1' already exists");
        }
    }

    // Tests adding new product
    @Test
    public void testProductAdd2() throws ApiException {
        String barcode = "barcode1", name = "name1";
        Double mrp = 100.15;
        Integer brandId = 1;
        ProductPojo productPojo = apiTestUtil.createProduct(barcode, brandId, name, mrp);
        productService.add(productPojo, brandId);

        ProductPojo existingProductPojo = productDao.get(productPojo.getId());
        assertEquals(existingProductPojo.getBarcode(), barcode);
        assertEquals(existingProductPojo.getBrandCategory(), brandId);
        assertEquals(existingProductPojo.getName(), name);
        assertEquals(existingProductPojo.getMrp(), mrp);
    }

    // Tests adding new product list
    @Test
    public void testProductAdd3() throws ApiException {
        String barcode1 = "barcode1", name1 = "name1";
        String barcode2 = "barcode2", name2 = "name2";
        Double mrp1 = 100.15, mrp2 = 120.15;
        Integer brandId = 1;
        ProductPojo productPojo1 = apiTestUtil.createProduct(barcode1, brandId, name1, mrp1);
        ProductPojo productPojo2 = apiTestUtil.createProduct(barcode2, brandId, name2, mrp2);
        List<ProductPojo> productPojoList = new ArrayList<>();
        productPojoList.add(productPojo1);
        productPojoList.add(productPojo2);
        productService.addList(productPojoList);

        List<ProductPojo> productPojoList1 = productDao.getAll();
        assertEquals(2, productPojoList1.size());
        assertEquals(productPojoList, productPojoList1);
    }

    // Tests adding duplicate product list
    @Test
    public void testProductAdd4() throws ApiException {
        String barcode1 = "barcode1", name1 = "name1";
        String barcode2 = "barcode2", name2 = "name2";
        String barcode3 = "barcode1", name3 = "name1";

        Double mrp1 = 100.15, mrp2 = 120.15;
        Integer brandId = 1;
        apiTestUtil.addProduct(barcode1, brandId, name1, mrp1);

        ProductPojo productPojo1 = apiTestUtil.createProduct(barcode2, brandId, name1, mrp1);
        ProductPojo productPojo2 = apiTestUtil.createProduct(barcode3, brandId, name2, mrp2);
        List<ProductPojo> productPojoList = new ArrayList<>();
        productPojoList.add(productPojo1);
        productPojoList.add(productPojo2);
        try {
            productService.addList(productPojoList);
            fail("Adding duplicate products worked");
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("Duplicate entry: 'barcode1'", errorList.get(1));
        }
    }

    // Tests fetching non-existent product
    @Test
    public void testProductGet1() {
        try {
            ProductPojo productPojo = productService.getCheck(100);
            fail("Fetching non-existent product worked");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Product doesn't exist");
        }
    }

    // Tests fetching existing product by id
    @Test
    public void testProductGet2() throws ApiException {
        String barcode = "barcode1", name = "name1";
        Double mrp = 100.15;
        Integer id = 1;
        ProductPojo productPojo = apiTestUtil.addProduct(barcode, 1, name, mrp);

        ProductPojo existingProductPojo = productService.getCheck(productPojo.getId());
        assertEquals(productPojo, existingProductPojo);
    }

    // Tests fetching existing product by barcode
    @Test
    public void testProductGet3() throws ApiException {
        String barcode = "barcode1", name = "name1";
        Double mrp = 100.15;
        Integer id = 1;
        ProductPojo productPojo = apiTestUtil.addProduct(barcode, 1, name, mrp);

        ProductPojo existingProductPojo = productService.getCheck(barcode);
        assertEquals(productPojo, existingProductPojo);
    }

    // Tests fetching existing products by barcode list
    @Test
    public void testProductGet4() throws ApiException {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

       List<ProductPojo> existingProductPojoList = productService.getCheckByBarcodeList(barcodeList);
        assertEquals(productPojoList, existingProductPojoList);
    }

    // Tests fetching non-existent products by barcode list
    @Test
    public void testProductGet5() throws ApiException {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

        try {
            List<ProductPojo> existingProductPojoList = productService.getCheckByBarcodeList(Arrays.asList("barcode1", "barcode3"));
            fail();
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("No product registered with barcode 'barcode3' found", errorList.get(1));
        }
    }

    // Tests fetching all products
    @Test
    public void testProductGet6() {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

        List<ProductPojo> existingProductPojoList = productService.getAll();
        assertEquals(productPojoList, existingProductPojoList);
    }

    // Tests fetching paginated list of products
    @Test
    public void testProductGet7() {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2", "barcode3", "barcode4");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

        List<ProductPojo> existingProductPojoList = productService.getPaginatedList(1, 2);
        assertEquals(productPojoList.subList(1, 3), existingProductPojoList);
    }

    // Tests fetching count of products
    @Test
    public void testProductGet8() {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

        Integer cnt = 2;
        Integer productCount = productService.getCount();
        assertEquals(cnt, productCount);
    }

    // Tests fetching products by id list
    @Test
    public void testProductGet9() {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

        List<ProductPojo> productPojoList1 = productService.getAllByIdList(productPojoList.stream().map(ProductPojo::getId).collect(Collectors.toList()));
        assertEquals(productPojoList, productPojoList1);
    }

    // Tests fetching map by barcode list
    @Test
    public void testProductGet10() {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

        Map<String, Integer> barcodeToProductIdMap = productService.getBarcodeToProductIdMap(productPojoList.stream().map(ProductPojo::getBarcode).collect(Collectors.toList()));
        assertEquals(productPojoList.get(0).getId(), barcodeToProductIdMap.get("barcode1"));
        assertEquals(productPojoList.get(1).getId(), barcodeToProductIdMap.get("barcode2"));
    }

    // Tests fetching product list by empty id list
    @Test
    public void testProductGet11() {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23);

        List<ProductPojo> productPojoList1 = productService.getAllByIdList(new ArrayList<>());
        assertEquals(0, productPojoList1.size());
    }

    // Tests fetching product list by brand id list
    @Test
    public void testProductGet12() {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = new ArrayList<>();
        productPojoList.add(apiTestUtil.addProduct(barcodeList.get(0), 1, "name", 12.12));
        productPojoList.add(apiTestUtil.addProduct(barcodeList.get(1), 2, "name", 12.12));

        List<ProductPojo> productPojoList1 = productService.getAllByBrandIdList(Arrays.asList(1, 2));
        assertEquals(2, productPojoList1.size());
        assertEquals(productPojoList1.get(0), productPojoList.get(0));
        assertEquals(productPojoList1.get(1), productPojoList.get(1));
    }

    // Tests fetching product list by empty id list
    @Test
    public void testProductGet13() {
        List<ProductPojo> productPojoList1 = productService.getAllByBrandIdList(new ArrayList<>());
        assertEquals(0, productPojoList1.size());
    }

    // Tests fetching product list by empty id list
    @Test
    public void testProductGet14() {
        List<ProductPojo> productPojoList1 = productService.getAllByIdList(new ArrayList<>());
        assertEquals(0, productPojoList1.size());
    }

    // Tests updating non-existent products
    @Test
    public void testProductUpdate1() throws ApiException {
        try {
            productService.update(100, "name", 12.12);
            fail();
        } catch (ApiException e) {
            assertEquals("Product doesn't exist", e.getMessage());
        }
    }

    // Tests updating product
    @Test
    public void testProductUpdate2() throws ApiException {
        String barcode1 = "barcode1", name1 = "name1";
        Double mrp1 = 100.15;
        Integer id = 1;
        ProductPojo productPojo1 = apiTestUtil.addProduct(barcode1, 1, name1, mrp1);

        String name2 = "name1";
        Double mrp2 = 50.15;

        ProductPojo productPojo2 = productService.update(productPojo1.getId(), name2, mrp2);
        assertEquals(productPojo2.getBarcode(), barcode1);
        assertEquals(productPojo2.getBrandCategory(), id);
        assertEquals(productPojo2.getName(), name2);
        assertEquals(productPojo2.getMrp(), mrp2);
    }

}
