package com.increff.pos.dto;

import com.google.gson.Gson;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderUpdateForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Log4j
public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderItemService orderItemService;

    // Tests when order item barcodes don't match
    @Test
    public void testAdd1() {
        String brand1 = "brand1", category1 = "category1";
        BrandPojo brandPojo = apiTestUtil.addBrand(brand1, category1);

        String barcode1 = "barcode1", name1 = "productName1";
        ProductPojo productPojo1 = apiTestUtil.addProduct(barcode1, brandPojo.getId(), name1, 99.99);

        String barcode2 = "barcode2", name2 = "productName2";
        ProductPojo productPojo2 = apiTestUtil.addProduct(barcode2, brandPojo.getId(), name2, 49.99);

        InventoryPojo inventoryPojo1 = apiTestUtil.addInventory(productPojo1.getId(), 100);
        InventoryPojo inventoryPojo2 = apiTestUtil.addInventory(productPojo2.getId(), 100);



        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(apiTestUtil.getOrderItemForm("barcode1", 5, 95.99));
        orderItemFormList.add(apiTestUtil.getOrderItemForm("barcode3", 3, 45.99));

        String name = "order1";
        OrderForm orderForm = apiTestUtil.getOrderForm(name, orderItemFormList);
        try {
            orderDto.add(orderForm);
            fail("Adding order should have failed");
        } catch (ApiException e) {
            List<String> errorList = new Gson().fromJson(e.getMessage(), List.class);
            assertEquals("No product registered with barcode 'barcode3' found", errorList.get(1));
        }
    }

    // Tests when order is added successfully
    @Test
    public void testAdd2() throws ApiException {
        String brand1 = "brand1", category1 = "category1";
        BrandPojo brandPojo = apiTestUtil.addBrand(brand1, category1);

        String barcode1 = "barcode1", name1 = "productName1";
        ProductPojo productPojo1 = apiTestUtil.addProduct(barcode1, brandPojo.getId(), name1, 99.99);

        String barcode2 = "barcode2", name2 = "productName2";
        ProductPojo productPojo2 = apiTestUtil.addProduct(barcode2, brandPojo.getId(), name2, 49.99);

        InventoryPojo inventoryPojo1 = apiTestUtil.addInventory(productPojo1.getId(), 100);
        InventoryPojo inventoryPojo2 = apiTestUtil.addInventory(productPojo2.getId(), 100);


        Integer quantity1 = 3, quantity2 = 5;
        Double sellingPrice1 = 95.99, sellingPrice2 = 45.99;
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        orderItemFormList.add(apiTestUtil.getOrderItemForm("barcode1", quantity1, sellingPrice1));
        orderItemFormList.add(apiTestUtil.getOrderItemForm("barcode2", quantity2, sellingPrice2));

        String name = "order1";
        OrderForm orderForm = apiTestUtil.getOrderForm(name, orderItemFormList);
        orderDto.add(orderForm);

        List<OrderPojo> orderPojoList = apiTestUtil.getAllOrders();
        assertEquals(1, orderPojoList.size());
        assertEquals(name, orderPojoList.get(0).getName());
        assertEquals(OrderStatus.CREATED, orderPojoList.get(0).getStatus());

        List<OrderItemPojo> orderItemPojoList = apiTestUtil.getAllOrderItems(orderPojoList.get(0).getId());
        assertEquals(productPojo1.getId(), orderItemPojoList.get(0).getProductId());
        assertEquals(orderPojoList.get(0).getId(),  orderItemPojoList.get(0).getOrderId());
        assertEquals(quantity1, orderItemPojoList.get(0).getQuantity());
        assertEquals(sellingPrice1, orderItemPojoList.get(0).getSellingPrice());

        assertEquals(productPojo2.getId(), orderItemPojoList.get(1).getProductId());
        assertEquals(orderPojoList.get(0).getId(),  orderItemPojoList.get(1).getOrderId());
        assertEquals(quantity2, orderItemPojoList.get(1).getQuantity());
        assertEquals(sellingPrice2, orderItemPojoList.get(1).getSellingPrice());
    }

    // Tests when no order is present
    public void testGet1() {
        Integer id = 100;
        try {
            orderDto.getCheck(id);
            fail("Non existing order was fetched");
        } catch (ApiException e) {
            assertEquals("Order doesn't exist", e.getMessage());
        }
    }

    // Tests fetching correct order
    @Test
    public void testGet2() throws ApiException {
        String name = "order1";
        OrderPojo createdOrder = apiTestUtil.addOrder(name, OrderStatus.CREATED);
        List<OrderItemPojo> orderItemPojoList = apiTestUtil.addOrderItemList(
                Arrays.asList(createdOrder.getId(), createdOrder.getId()),
                Arrays.asList(1, 2),
                2,
                23.23
        );


        OrderData orderData = orderDto.getCheck(createdOrder.getId());
        assertEquals(name, orderData.getName());
        assertEquals(OrderStatus.CREATED, orderData.getStatus());
        assertEquals(2, orderData.getOrderItemDataList().size());
    }

    // Tests fetching paginated list
    @Test
    public void testGet3() throws ApiException {
        String name = "order1";
        OrderPojo createdOrder = apiTestUtil.addOrder(name, OrderStatus.CREATED);
        List<OrderItemPojo> orderItemPojoList = apiTestUtil.addOrderItemList(
                IntStream.range(0, 4).boxed().collect(Collectors.toList()),
                IntStream.range(2, 6).boxed().collect(Collectors.toList()),
                2,
                23.23
        );

        Integer draw = 1, recordsTotal = 1, recordsFiltered = 1;
        PaginationData<OrderData> paginationData = orderDto.getPaginatedList(draw, 0, 1);
        assertEquals(draw, paginationData.getDraw());
        assertEquals(recordsTotal, paginationData.getRecordsTotal());
        assertEquals(recordsFiltered, paginationData.getRecordsFiltered());
        assertEquals(1, paginationData.getData().size());
    }

    // Tests updating order
    @Test
    public void testUpdate1() throws ApiException {
        OrderPojo orderPojo = apiTestUtil.addOrder("name", OrderStatus.CREATED);

        OrderUpdateForm orderUpdateForm = new OrderUpdateForm();
        orderUpdateForm.setName("newName");

        orderDto.update(orderPojo.getId(), orderUpdateForm);

        assertEquals(orderUpdateForm.getName(), orderPojo.getName());
    }

}
