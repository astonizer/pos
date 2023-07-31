package com.increff.pos.dto;

import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.OrderItemUpdateForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;

public class OrderItemDtoTest extends AbstractUnitTest {

    @Autowired
    private OrderItemDto orderItemDto;

    @Autowired
    private ApiTestUtil apiTestUtil;

    // Tests updating order item
    @Test
    public void testUpdate1() throws ApiException {
        ProductPojo productPojo = apiTestUtil.addProduct("barcode", 1, "name", 12.12);
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(productPojo.getId(), 2);
        OrderPojo orderPojo = apiTestUtil.addOrder("name", OrderStatus.CREATED);

        OrderItemPojo orderItemPojo = apiTestUtil.addOrderItem(orderPojo.getId(), productPojo.getId(), 1, 12.23);
        OrderItemUpdateForm orderItemUpdateForm = apiTestUtil.getOrderItemUpdateForm(orderPojo.getId(), productPojo.getBarcode(), 3);

        orderItemDto.update(orderItemPojo.getId(), orderItemUpdateForm);
        assertEquals((Integer)3, orderItemPojo.getQuantity());
        assertEquals((Integer)0, inventoryPojo.getQuantity());
    }

    // Tests deleting order item
    @Test
    public void testDelete1() throws ApiException {
        InventoryPojo inventoryPojo = apiTestUtil.addInventory(1, 5);
        OrderPojo orderPojo = apiTestUtil.addOrder("name", OrderStatus.CREATED);
        OrderItemPojo orderItemPojo = apiTestUtil.addOrderItem(orderPojo.getId(), 1, 1, 12.12);

        orderItemDto.delete(orderItemPojo.getId());

        assertEquals((Integer)6, inventoryPojo.getQuantity());
        assertEquals((Integer)0, orderItemPojo.getQuantity());
    }

}
