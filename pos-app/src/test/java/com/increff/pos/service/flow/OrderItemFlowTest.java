package com.increff.pos.service.flow;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class OrderItemFlowTest extends AbstractUnitTest {

    @Autowired
    private OrderItemFlow orderItemFlow;

    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private OrderItemDao orderItemDao;

    // Tests updating order item
    @Test
    public void testUpdate1() throws ApiException {
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(1, 5);
        OrderPojo orderPojo = apiTestUtil.addOrder("name", OrderStatus.CREATED);
        OrderItemPojo orderItemPojo = apiTestUtil.addOrderItem(orderPojo.getId(), 1, 1, 1.5);

        Integer added = 2, newQuantity = 4;
        orderItemFlow.update(orderItemPojo.getId(), 1, newQuantity);
        assertEquals(added, inventoryPojo.getQuantity());
        assertEquals(newQuantity, orderItemPojo.getQuantity());
    }

    // Tests updating order item to invalid quantity
    @Test
    public void testUpdate2() throws ApiException {
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(1, 5);
        OrderPojo orderPojo = apiTestUtil.addOrder("name", OrderStatus.CREATED);
        OrderItemPojo orderItemPojo = apiTestUtil.addOrderItem(orderPojo.getId(), 1, 1, 1.5);

        Integer newQuantity = 7;
        try {
            orderItemFlow.update(orderItemPojo.getId(), 1, newQuantity);
            fail();
        } catch (ApiException e) {
            assertEquals("Insufficient inventory for this product.", e.getMessage());
        }
    }

    // Tests deleting order item
    @Test
    public void testDelete1() throws ApiException {
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(1, 5);
        OrderPojo orderPojo = apiTestUtil.addOrder("name", OrderStatus.CREATED);
        OrderItemPojo orderItemPojo = apiTestUtil.addOrderItem(orderPojo.getId(), 1, 1, 12.12);

        orderItemFlow.delete(orderItemPojo.getId());

        assertEquals((Integer)6, inventoryPojo.getQuantity());
        assertEquals((Integer)0, orderItemPojo.getQuantity());
    }

}
