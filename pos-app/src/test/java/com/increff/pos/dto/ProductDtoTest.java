package com.increff.pos.dto;

import com.google.gson.Gson;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class ProductDtoTest extends AbstractUnitTest {
    
    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ProductDao productDao;

    // Tests if product is received correctly
    @Test
    public void testProductAdd1() throws ApiException {
        String barcode = "barcode", brand = "brand", category = "category", name = "name";
        Double mrp = 12.12;
        Integer brandId = apiTestUtil.addBrand(brand, category).getId();

        ProductData productData = productDto.add(apiTestUtil.getProductForm(barcode, brand, category, name, mrp));
        ProductPojo productPojo = productDao.get(productData.getId());
        assertProductPojo(productPojo, barcode, brandId, name, mrp);
    }

    // Tests if product list is received correct
    @Test
    public void testProductAdd2() throws ApiException {
        List<String> barcodeList = IntStream.range(0, 5).mapToObj(id -> ("barcode" + id)).collect(Collectors.toList());
        String brand = "brand", category = "category", name = "name";
        Double mrp = 12.12;
        Integer brandId = apiTestUtil.addBrand(brand, category).getId();
        List<ProductForm> productFormList = apiTestUtil.getAddProductFormList(barcodeList, brand, category, name, mrp);

        productDto.addList(productFormList);
        List<ProductPojo> productPojoList = apiTestUtil.getAllProducts();
        IntStream.range(0, 5).forEach(id -> {
            ProductPojo productPojo = productPojoList.get(id);
            assertProductPojo(productPojo, barcodeList.get(id), brandId, name, mrp);
        });
    }

    // Tests duplicated checking of product list
    @Test
    public void testProductAdd3() {
        List<String> barcodeList = IntStream.range(0, 5).mapToObj(id -> ("barcode" + id)).collect(Collectors.toList());
        barcodeList.add("barcode1");
        String brand = "brand", category = "category", name = "name";
        Double mrp = 12.12;
        Integer brandId = apiTestUtil.addBrand(brand, category).getId();
        List<ProductForm> productFormList = apiTestUtil.getAddProductFormList(barcodeList, brand, category, name, mrp);

        try {
            productDto.addList(productFormList);
            fail();
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("Duplicate entry: 'barcode1'", errorList.get(5));
        }
    }

    // Tests invalid brands of product list
    @Test
    public void testProductAdd4() {
        List<String> barcodeList = IntStream.range(0, 5).mapToObj(id -> ("barcode" + id)).collect(Collectors.toList());
        String brand = "brand", category = "category", name = "name";
        Double mrp = 12.12;
        Integer brandId = apiTestUtil.addBrand(brand, category).getId();
        List<ProductForm> productFormList = apiTestUtil.getAddProductFormList(barcodeList, brand, category, name, mrp);
        productFormList.add(apiTestUtil.getProductForm("barcode5", "b", "c", name, mrp));

        try {
            productDto.addList(productFormList);
            fail();
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("Brand b - c doesn't exist", errorList.get(5));
        }
    }

    // Tests getting product
    @Test
    public void testProductGet1() throws ApiException {
        String barcode = "barcode", brand = "brand", category = "category", name = "name";
        Double mrp = 12.12;
        Integer brandId = apiTestUtil.addBrand(brand, category).getId();
        ProductPojo productPojo = apiTestUtil.addProduct(barcode, brandId, name, mrp);

        ProductData productData = productDto.getCheck(productPojo.getId());
        assertProductData(productData, barcode, brand, category, name, mrp);
    }

    // Tests getting paginated list
    @Test
    public void testProductGet2() throws ApiException {
        List<String> barcodeList = IntStream.range(0, 5).mapToObj(id -> ("barcode" + id)).collect(Collectors.toList());
        String brand = "brand", category = "category", name = "name";
        Double mrp = 12.12;
        Integer brandId = apiTestUtil.addBrand(brand, category).getId();
        List<ProductPojo> productPojoList = apiTestUtil.addProductList(barcodeList, brandId, name, mrp);

        Integer draw = 1, recordsFiltered = 5, recordsTotal = 5;
        PaginationData<ProductData> paginationData = productDto.getPaginatedList(1, 2, 2);
        assertEquals(draw, paginationData.getDraw());
        assertEquals(recordsFiltered, paginationData.getRecordsFiltered());
        assertEquals(recordsTotal, paginationData.getRecordsTotal());
        IntStream.range(2, 4).forEach(id -> {
            ProductData productData = paginationData.getData().get(id-2);
            assertProductData(productData, barcodeList.get(id), brand, category, name, mrp);
        });
    }

    // Tests updating product
    @Test
    public void testProductUpdate1() throws ApiException {
        String barcode = "barcode", brand = "brand", category = "category", name = "name";
        Double mrp = 12.12;
        Integer brandId = apiTestUtil.addBrand(brand, category).getId();
        ProductPojo productPojo = apiTestUtil.addProduct(barcode, brandId, name, mrp);

        String newName = "name1";
        Double newMrp = 43.34;

        productDto.update(productPojo.getId(), apiTestUtil.getProductUpdateForm(newName, newMrp));
        assertProductPojo(productPojo, barcode, brandId, newName, newMrp);
    }

    private void assertProductData(ProductData productData, String barcode, String brand, String category, String name, Double mrp) {
        assertEquals(barcode, productData.getBarcode());
        assertEquals(brand, productData.getBrand());
        assertEquals(category, productData.getCategory());
        assertEquals(name, productData.getName());
        assertEquals(mrp, productData.getMrp());
    }

    private void assertProductPojo(ProductPojo productPojo, String barcode, Integer brandId, String name, Double mrp) {
        assertEquals(barcode, productPojo.getBarcode());
        assertEquals(brandId, productPojo.getBrandCategory());
        assertEquals(name, productPojo.getName());
        assertEquals(mrp, productPojo.getMrp());
    }
    
}
