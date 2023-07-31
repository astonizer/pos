package com.increff.pos.service;

import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.*;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.DaoUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class OrderServiceTest extends AbstractUnitTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DaoUtil daoApi;

//     Tests if order is added
    @Test
    public void testOrderAdd1() {
        String name = "name";
        OrderPojo orderPojo = orderService.add(name);

        List<OrderPojo> orderPojoList = daoApi.getAllOrders();
        assertEquals(1, orderPojoList.size());
        assertEquals(orderPojo.getId(), orderPojoList.get(0).getId());
        assertEquals("name", orderPojoList.get(0).getName());
    }

    // Tests fetching non-existent order
    @Test
    public void testOrderGet1() {
        Integer id = 100;

        try {
            orderService.getCheck(id);
            fail("Fetching non-existent order worked");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Order doesn't exist");
        }
    }

    // Tests fetching existing order
    @Test
    public void testOrderGet2() throws ApiException {
        String name = "name";
        OrderPojo orderPojo = daoApi.addOrder(name, OrderStatus.CREATED);

        OrderPojo orderPojo1 = orderService.getCheck(orderPojo.getId());
        assertEquals(orderPojo.getId(), orderPojo1.getId());
        assertEquals("name", orderPojo1.getName());
        assertEquals(OrderStatus.CREATED, orderPojo1.getStatus());
    }

    // Tests fetching paginated orders
    @Test
    public void testOrderGet3() {
        List<String> nameList = IntStream.range(0, 10).mapToObj(num -> ("name" + num)).collect(Collectors.toList());
        List<OrderPojo> orderPojoList = nameList.stream().map(orderService::add).collect(Collectors.toList());

        List<OrderPojo> page1OrderList = orderService.getPaginatedList(5, 5);
        IntStream.range(5, 10).forEach(id -> {
            OrderPojo orderPojo1 = orderPojoList.get(id);
            OrderPojo orderPojo2 = page1OrderList.get(id - 5);

            assertEquals(orderPojo1.getId(), orderPojo2.getId());
            assertEquals(orderPojo1.getName(), orderPojo2.getName());
            assertEquals(orderPojo1.getStatus(), orderPojo2.getStatus());
        });
    }

    // Tests fetching total count of orders
    @Test
    public void testOrderGet4() {
        Integer totalCount = 7;
        List<String> nameList = IntStream.range(0, totalCount).mapToObj(num -> ("name" + num)).collect(Collectors.toList());
        nameList.stream().forEach(orderService::add);

        Integer orderCount = orderService.getCount();
        assertEquals(totalCount, orderCount);
    }

    // Tests fetching invoiced orders in range
    @Test
    public void testOderGet5() {
        String name1 = "name1";
        OrderStatus status1 = OrderStatus.INVOICED;
        ZonedDateTime updatedAt1 = ZonedDateTime.now().minusDays(2);
        OrderPojo orderPojo1 = daoApi.addOrder(name1, status1, updatedAt1);

        String name2 = "name2";
        OrderStatus status2 = OrderStatus.INVOICED;
        ZonedDateTime updatedAt2 = ZonedDateTime.now();
        OrderPojo orderPojo2 = daoApi.addOrder(name2, status2, updatedAt2);

        String name3 = "name3";
        OrderStatus status3 = OrderStatus.INVOICED;
        ZonedDateTime updatedAt3 = ZonedDateTime.now().plusDays(2);
        OrderPojo orderPojo3 = daoApi.addOrder(name3, status3, updatedAt3);

        List<OrderPojo> orderPojoList = orderService.findInvoicedOrdersInRange(updatedAt1.plusDays(1), updatedAt3.minusDays(1));
        assertEquals(1, orderPojoList.size());
        assertEquals(orderPojo2.getId(), orderPojoList.get(0).getId());
        assertEquals(name2, orderPojo2.getName());
        assertEquals(status2, orderPojo2.getStatus());
    }

    // Tests updating non-existent order
    @Test
    public void testOrderUpdate1() {
        Integer id = 100;
        String name = "name";

        try {
            orderService.update(id, name);
            fail("Updating non-existent order worked");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Order doesn't exist");
        }
    }

    // Tests updating existing order
    @Test
    public void testOrderUpdate2() throws ApiException {
        String name = "name1";
        OrderPojo orderPojo = daoApi.addOrder(name, OrderStatus.CREATED);

        String newName = "name2";

        orderService.update(orderPojo.getId(), newName);
        List<OrderPojo> orderPojoList = daoApi.getAllOrders();
        assertEquals(1, orderPojoList.size());
        assertEquals(orderPojo.getId(), orderPojoList.get(0).getId());
        assertEquals(newName, orderPojoList.get(0).getName());
    }

    // Tests placing non-existent order
    @Test
    public void testOrderUpdate3() {
        Integer id = 100;

        try {
            orderService.invoiceOrder(id);
            fail("Updating non-existent order worked");
        } catch(ApiException e) {
            assertEquals(e.getMessage(), "Order doesn't exist");
        }
    }

    // Tests placing order
    @Test
    public void testOrderUpdate4() throws ApiException {
        String name = "name1";
        OrderPojo orderPojo = daoApi.addOrder(name, OrderStatus.CREATED);

        orderService.invoiceOrder(orderPojo.getId());
        assertEquals(OrderStatus.INVOICED, orderPojo.getStatus());
    }

}
