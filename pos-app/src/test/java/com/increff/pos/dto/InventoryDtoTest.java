package com.increff.pos.dto;

import com.google.gson.Gson;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
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
import static junit.framework.TestCase.fail;

public class InventoryDtoTest extends AbstractUnitTest  {

    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private InventoryDao inventoryDao;

    // Tests increasing quantity
    @Test
    public void testUpdate1() throws ApiException {
        String barcode = "barcode", name = "name";
        Integer brandCategory = 1, quantity = 5;
        Double mrp = 12.12;
        Integer productId = apiTestUtil.addProduct(barcode, brandCategory, name, mrp).getId();
        apiTestUtil.addEmptyInventory(productId);

        InventoryData inventoryData = inventoryDto.increaseQuantity(apiTestUtil.getInventoryForm(barcode, quantity));

        InventoryPojo inventoryPojo = inventoryDao.get(inventoryData.getProductId());
        assertInventoryPojo(inventoryPojo, quantity);
    }

    // Tests uploading inventory form list
    @Test
    public void testAdd1() throws ApiException {
        List<String> barcodeList = IntStream.range(0, 5).mapToObj(id -> ("barcode"+id)).collect(Collectors.toList());
        Integer brandCategory = 1;
        String name = "name";
        Double mrp = 12.12;
        List<Integer> productIdList = apiTestUtil.addProductList(barcodeList, brandCategory, name, mrp).stream().map(ProductPojo::getId).collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(5, 10).boxed().collect(Collectors.toList());
        apiTestUtil.addInventoryList(productIdList, 2);

        inventoryDto.increaseQuantityOfList(apiTestUtil.getInventoryFormList(barcodeList, quantityList));
        List<InventoryPojo> inventoryPojoList = apiTestUtil.getAllInventory();
        IntStream.range(0, 5).forEach(id -> {
           assertInventoryPojo(inventoryPojoList.get(id), quantityList.get(id) + 2);
        });
    }

    // Tests uploading invalid barcode in inventory form list
    @Test
    public void testAdd2() {
        List<String> barcodeList = IntStream.range(0, 5).mapToObj(id -> ("barcode"+id)).collect(Collectors.toList());
        Integer brandCategory = 1;
        String name = "name";
        Double mrp = 12.12;
        apiTestUtil.addProductList(barcodeList, brandCategory, name, mrp);
        List<Integer> quantityList = IntStream.range(5, 11).boxed().collect(Collectors.toList());

        barcodeList.add("barcode5");

        try {
            inventoryDto.increaseQuantityOfList(apiTestUtil.getInventoryFormList(barcodeList, quantityList));
            fail();
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("No product registered with barcode 'barcode5' found", errorList.get(5));
        }
    }

    // Tests getting inventory
    @Test
    public void testGet1() throws ApiException {
        String barcode = "barcode", name = "name";
        Integer brandCategory = 1, quantity = 5;
        Double mrp = 12.12;
        Integer productId = apiTestUtil.addProduct(barcode, brandCategory, name, mrp).getId();
        apiTestUtil.addInventory(productId, quantity);

        InventoryData inventoryData = inventoryDto.getCheck(productId);
        assertInventoryData(inventoryData, quantity);
    }

    // Tests getting paginated list
    @Test
    public void testGet2() throws ApiException {
        List<Integer> productIdList = IntStream.range(0, 5).boxed().collect(Collectors.toList());
        Integer quantity = 5;
        apiTestUtil.addInventoryList(productIdList, quantity);

        Integer draw = 1, recordsFiltered = 5, recordsTotal = 5;
        PaginationData<InventoryData> paginationData = inventoryDto.getPaginatedList(1, 2, 2);
        assertEquals(draw, paginationData.getDraw());
        assertEquals(recordsFiltered, paginationData.getRecordsFiltered());
        assertEquals(recordsTotal, paginationData.getRecordsTotal());
        IntStream.range(2, 4).forEach(id -> {
            InventoryData inventoryData = paginationData.getData().get(id-2);
            assertInventoryData(inventoryData, quantity);
        });
    }

    // Tests updating quantity
    @Test
    public void testUpdate2() throws ApiException {
        String barcode = "barcode", name = "name";
        Integer brandCategory = 1, quantity = 5;
        Double mrp = 12.12;
        Integer productId = apiTestUtil.addProduct(barcode, brandCategory, name, mrp).getId();
        apiTestUtil.addInventory(productId, quantity);

        Integer newQuantity = 8;
        InventoryData inventoryData = inventoryDto.updateQuantity(productId, apiTestUtil.getInventoryUpdateForm(newQuantity));

        InventoryPojo inventoryPojo = inventoryDao.get(inventoryData.getProductId());
        assertInventoryPojo(inventoryPojo, newQuantity);
    }

    // Tests checking quantity for inventory
    @Test
    public void testCheck1() throws ApiException {
        ProductPojo productPojo = apiTestUtil.addProduct("barcode", 1, "name", 12.12);
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(productPojo.getId(), 10);

        InventoryForm inventoryForm = apiTestUtil.getInventoryForm(productPojo.getBarcode(), 2);

        InventoryData inventoryData = inventoryDto.checkQuantity(inventoryForm);
        assertEquals(inventoryPojo.getQuantity(), inventoryData.getQuantity());
    }

    // Tests checking quantity for inventory when required quantity is greater
    @Test
    public void testCheck2() {
        ProductPojo productPojo = apiTestUtil.addProduct("barcode", 1, "name", 12.12);
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(productPojo.getId(), 10);

        InventoryForm inventoryForm = apiTestUtil.getInventoryForm(productPojo.getBarcode(), 11);

        try {
            InventoryData inventoryData = inventoryDto.checkQuantity(inventoryForm);
        } catch (ApiException e) {
            assertEquals("Required inventory for this order item is currently unavailable.", e.getMessage());
        }
    }

    // Tests checking quantity for inventory when required quantity is zero
    @Test
    public void testCheck3() {
        ProductPojo productPojo = apiTestUtil.addProduct("barcode", 1, "name", 12.12);
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(productPojo.getId(), 0);

        InventoryForm inventoryForm = apiTestUtil.getInventoryForm(productPojo.getBarcode(), 11);

        try {
            InventoryData inventoryData = inventoryDto.checkQuantity(inventoryForm);
        } catch (ApiException e) {
            assertEquals("This item is currently out of stock.", e.getMessage());
        }
    }

    private void assertInventoryPojo(InventoryPojo inventoryPojo, Integer quantity) {
        assertEquals(quantity, inventoryPojo.getQuantity());
    }

    private void assertInventoryData(InventoryData inventoryData, Integer quantity) {
        assertEquals(quantity, inventoryData.getQuantity());
    }

}
