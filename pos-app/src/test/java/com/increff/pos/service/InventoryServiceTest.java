package com.increff.pos.service;

import com.google.gson.Gson;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class InventoryServiceTest extends AbstractUnitTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ApiTestUtil apiTestUtil;

    // Tests adding empty inventory for duplicate product
    @Test
    public void testInventoryAdd1() {
        Integer productId = 1, quantity = 5;
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(productId, quantity);

        try {
            inventoryService.addEmptyInventory(productId);
            fail("Adding duplicate inventory worked");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Product already exists");
        }
    }

    // Tests adding empty inventory for new product
    @Test
    public void testInventoryAdd2() throws ApiException {
        Integer productId = 1, quantity = 0;
        inventoryService.addEmptyInventory(productId);

        InventoryPojo inventoryPojo = inventoryService.getCheck(productId);
        assertEquals(inventoryPojo.getQuantity(), quantity);
    }

    // Tests adding empty inventory for new product list
    @Test
    public void testInventoryAdd3() throws ApiException {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<Integer> productIdList = apiTestUtil.addProductList(barcodeList, 1, "name", 23.23)
                .stream().map(ProductPojo::getId).collect(Collectors.toList());

        List<InventoryPojo> inventoryPojoList = inventoryService.addEmptyInventory(productIdList);

        Integer quantity = 0;
        assertEquals(2, inventoryPojoList.size());
        assertEquals(quantity, inventoryPojoList.get(0).getQuantity());
        assertEquals(quantity, inventoryPojoList.get(1).getQuantity());
    }

    // Tests fetching non-existent inventory
    @Test
    public void testInventoryGet1() {
        Integer productId = 100;

        try {
            inventoryService.getCheck(productId);
            fail("Fetching non-existent inventory worked");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Product doesn't exist");
        }
    }

    // Tests fetching existing inventory
    @Test
    public void testInventoryGet2() throws ApiException {
        Integer productId = 1, quantity = 5;
        apiTestUtil.addInventory(productId, quantity);

        InventoryPojo inventoryPojo = inventoryService.getCheck(productId);
        assertEquals(inventoryPojo.getProductId(), productId);
        assertEquals(inventoryPojo.getQuantity(), quantity);
    }

    // Tests if fetching all inventory works fine
    @Test
    public void testInventoryGet3() {
        List<Integer> productIdList = IntStream.range(3, 5).boxed().collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(4, 6).boxed().collect(Collectors.toList());
        List<InventoryPojo> existingInventoryPojoList = apiTestUtil.addInventoryList(productIdList, quantityList);

        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        assertEquals(2, inventoryPojoList.size());
        IntStream.range(0, 2).forEach(id -> {
           assertEquals(existingInventoryPojoList.get(id), inventoryPojoList.get(id));
        });
    }

    // Tests if fetching paginated data
    @Test
    public void testInventoryGet4() {
        List<Integer> productIdList = IntStream.range(0, 5).boxed().collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(5, 10).boxed().collect(Collectors.toList());
        List<InventoryPojo> existingInventoryPojoList = apiTestUtil.addInventoryList(productIdList, quantityList);

        List<InventoryPojo> inventoryPojoList = inventoryService.getPaginatedList(2, 2);
        assertEquals(2, inventoryPojoList.size());
        IntStream.range(2, 4).forEach(id -> {
            assertEquals(existingInventoryPojoList.get(id), inventoryPojoList.get(id-2));
        });
    }

    // Tests fetching inventory count
    @Test
    public void testInventoryGet5() {
        List<Integer> productIdList = IntStream.range(3, 5).boxed().collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(4, 6).boxed().collect(Collectors.toList());
        List<InventoryPojo> existingInventoryPojoList = apiTestUtil.addInventoryList(productIdList, quantityList);

        Integer cnt = 2;
        Integer inventoryCount = inventoryService.getCount();
        assertEquals(cnt, inventoryCount);
    }

    // Tests checking for sufficient inventory when incoming inventory list is empty
    @Test
    public void testInventoryGet6() throws ApiException {
        List<InventoryPojo> inventoryPojoList = new ArrayList<>();

        inventoryService.checkIfSufficientInventoryExists(inventoryPojoList);
    }

    // Tests increasing non-existent inventory
    @Test
    public void testInventoryUpdate1() {
        Integer productId = 1, quantity = 5;

        try {
            inventoryService.increaseQuantity(productId, quantity);
            fail("Increasing quantity of non-existent inventory failed");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Product doesn't exist");
        }
    }

    // Tests increasing existing inventory
    @Test
    public void testInventoryUpdate2() throws ApiException {
        Integer productId = 1, quantity = 5;
        apiTestUtil.addInventory(productId, 0);

        InventoryPojo inventoryPojo = inventoryService.increaseQuantity(productId, quantity);
        assertEquals(inventoryPojo.getProductId(), productId);
        assertEquals(inventoryPojo.getQuantity(), quantity);
    }

    // Tests increasing inventory list
    @Test
    public void testInventoryUpdate3() throws ApiException {
        List<Integer> productIdList = IntStream.range(0, 5).boxed().collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(5, 10).boxed().collect(Collectors.toList());
        apiTestUtil.addInventoryList(productIdList, quantityList);

        List<InventoryPojo> inventoryPojoList = IntStream.range(3, 8).mapToObj(id -> {
            InventoryPojo inventoryPojo = new InventoryPojo();
            inventoryPojo.setProductId(productIdList.get(id-3));
            inventoryPojo.setQuantity(3);
            return inventoryPojo;
        }).collect(Collectors.toList());

        inventoryService.increaseQuantityOfList(inventoryPojoList);
        Integer increased = 8;

        List<InventoryPojo> inventoryPojoList1 = apiTestUtil.getAllInventory();
        IntStream.range(0, 5).boxed().forEach(id -> {
            Integer diff = id + increased;
            assertEquals(diff, inventoryPojoList1.get(id).getQuantity());
        });
    }

    // Tests decreasing non-existent inventory
    @Test
    public void testInventoryUpdate4() {
        Integer productId = 1, quantity = 5;

        try {
            inventoryService.increaseQuantity(productId, quantity);
            fail("Decreasing quantity of non-existent inventory failed");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Product doesn't exist");
        }
    }

    // Tests decreasing existing inventory
    @Test
    public void testInventoryUpdate5() throws ApiException {
        Integer productId = 1, actualQuantity = 0;
        apiTestUtil.addInventory(productId, 5);

        InventoryPojo inventoryPojo = inventoryService.decreaseQuantity(productId, 5);
        assertEquals(inventoryPojo.getProductId(), productId);
        assertEquals(inventoryPojo.getQuantity(), actualQuantity);
    }

    // Tests decreasing existing inventory list
    @Test
    public void testInventoryUpdate6() throws ApiException {
        List<Integer> productIdList = IntStream.range(0, 5).boxed().collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(5, 10).boxed().collect(Collectors.toList());
        apiTestUtil.addInventoryList(productIdList, quantityList);

        List<InventoryPojo> inventoryPojoList = IntStream.range(3, 8).mapToObj(id -> {
            InventoryPojo inventoryPojo = new InventoryPojo();
            inventoryPojo.setProductId(productIdList.get(id-3));
            inventoryPojo.setQuantity(id);
            return inventoryPojo;
        }).collect(Collectors.toList());

        inventoryService.decreaseQuantityOfList(inventoryPojoList);
        Integer diff = 2;

        List<InventoryPojo> inventoryPojoList1 = apiTestUtil.getAllInventory();
        IntStream.range(0, 5).forEach(id -> {
            assertEquals(diff, inventoryPojoList1.get(id).getQuantity());
        });
    }

    // Tests decreasing existing inventory list when insufficient quantity is present
    @Test
    public void testInventoryUpdate7() {
        List<Integer> productIdList = IntStream.range(0, 5).boxed().collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(5, 10).boxed().collect(Collectors.toList());
        apiTestUtil.addInventoryList(productIdList, quantityList);

        List<InventoryPojo> inventoryPojoList = IntStream.range(0, 5).mapToObj(id -> {
            InventoryPojo inventoryPojo = new InventoryPojo();
            inventoryPojo.setProductId(productIdList.get(id));
            inventoryPojo.setQuantity(7);
            return inventoryPojo;
        }).collect(Collectors.toList());

        try {
            inventoryService.decreaseQuantityOfList(inventoryPojoList);
            fail();
        } catch(ApiException e) {
            assertEquals("Insufficient inventory", e.getMessage());
        }
    }

    // Tests updating inventory
    @Test
    public void testInventoryUpdate8() throws ApiException {
        Integer quantity = 6;
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(3, 9);

        inventoryService.update(3, quantity);

        assertEquals(quantity, inventoryPojo.getQuantity());
    }

    // Tests checking sufficient inventory
    @Test
    public void testInventoryCheck1() {
        List<Integer> productIdList = IntStream.range(0, 3).boxed().collect(Collectors.toList());
        List<Integer> quantityList = IntStream.range(5, 8).boxed().collect(Collectors.toList());
        apiTestUtil.addInventoryList(productIdList, quantityList);

        List<Integer> productIdList1 = IntStream.range(0, 3).boxed().collect(Collectors.toList());
        List<Integer> quantityList1 = Arrays.asList(7, 7, 7);
        List<InventoryPojo> inventoryPojoList = apiTestUtil.createInventoryList(productIdList1, quantityList1);

        try {
            inventoryService.checkIfSufficientInventoryExists(inventoryPojoList);
            fail();
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("Insufficient quantity", errorList.get(0));
            assertEquals("Insufficient quantity", errorList.get(1));
            assertEquals(null, errorList.get(2));
        }
    }

}
