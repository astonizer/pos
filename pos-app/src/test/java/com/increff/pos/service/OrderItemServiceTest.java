package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;

public class OrderItemServiceTest extends AbstractUnitTest {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ApiTestUtil apiTestUtil;

    // Tests adding order item list
    @Test
    public void testAdd1() {
        List<Integer> orderIdList = Arrays.asList(1,1,2);
        List<Integer> productIdList = Arrays.asList(1,2,3);
        List<OrderItemPojo> orderItemPojoList1 = apiTestUtil.createOrderItemList(orderIdList, productIdList, 5, 23.23);

        orderItemService.add(orderItemPojoList1);

        List<OrderItemPojo> orderItemPojoList2 = orderItemDao.getAll();
        assertEquals(orderItemPojoList1.size(), orderItemPojoList2.size());
        IntStream.range(0, orderIdList.size()).forEach(
                id -> assertEquals(orderItemPojoList1.get(id), orderItemPojoList2.get(id)));
    }

    // Tests updating order item
    @Test
    public void testUpdate1() {
        OrderItemPojo orderItemPojo = apiTestUtil.addOrderItem(1, 1, 3, 34.34);

        Integer newQuantity = 4;
        OrderItemPojo orderItemPojo1 = orderItemService.update(orderItemPojo.getId(), newQuantity);

        assertEquals(orderItemPojo.getOrderId(), orderItemPojo1.getOrderId());
        assertEquals(orderItemPojo.getProductId(), orderItemPojo1.getProductId());
        assertEquals(orderItemPojo.getSellingPrice(), orderItemPojo1.getSellingPrice());
        assertEquals(newQuantity, orderItemPojo1.getQuantity());
    }

    // Tests deleting order items
    @Test
    public void testDelete1() {
        List<Integer> orderIdList = Arrays.asList(1,1,2);
        List<Integer> productIdList = Arrays.asList(1,2,3);
        List<OrderItemPojo> orderItemPojoList1 = apiTestUtil.addOrderItemList(orderIdList, productIdList, 5, 23.23);

        orderItemService.deleteByOrderId(1);

        List<OrderItemPojo> orderItemPojoList = orderItemDao.getAll();
        assertEquals(1, orderItemPojoList.size());
        assertEquals(orderItemPojoList1.get(2), orderItemPojoList.get(0));
    }

    // Tests getting an order item
    @Test
    public void testGet1() throws ApiException {
        OrderItemPojo orderItemPojo = apiTestUtil.addOrderItem(1, 1, 3, 34.34);

        OrderItemPojo orderItemPojo1 = orderItemService.getCheck(orderItemPojo.getId());

        assertEquals(orderItemPojo, orderItemPojo1);
    }

    // Tests getting order items by order id
    @Test
    public void testGet2() {
        List<Integer> orderIdList = Arrays.asList(1,1,2);
        List<Integer> productIdList = Arrays.asList(1,2,3);
        List<OrderItemPojo> orderItemPojoList1 = apiTestUtil.addOrderItemList(orderIdList, productIdList, 5, 23.23);

        List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsByOrderId(2);
        assertEquals(1, orderItemPojoList.size());
        assertEquals(orderItemPojoList1.get(2), orderItemPojoList.get(0));
    }

}
