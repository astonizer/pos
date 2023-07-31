package com.increff.pos.service;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.service.exception.ApiException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
@Log4j
public class OrderService extends AbstractService {
    
    @Autowired
    private OrderDao orderDao;

    public OrderPojo add(String name) {
        OrderPojo orderPojo = createOrder(name);

        return orderDao.save(orderPojo);
    }

    public OrderPojo getCheck(Integer id) throws ApiException {
        OrderPojo orderPojo = orderDao.get(id);

        checkNotNull(orderPojo, "Order doesn't exist");

        return orderPojo;
    }

    public OrderPojo getCheckCreatedOrder(Integer id) throws ApiException {
        OrderPojo orderPojo = orderDao.get(id);

        checkNotNull(orderPojo, "Order doesn't exist");

        if(orderPojo.getStatus() == OrderStatus.INVOICED) {
            throw new ApiException("Invoiced orders cannot be modified");
        }

        return orderPojo;
    }

    public List<OrderPojo> getPaginatedList(Integer start, Integer length) {
        return orderDao.getAllByPage(start, length);
    }

    public Integer getCount() {
        return orderDao.getCount();
    }

    public List<OrderPojo> findInvoicedOrdersInRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        return orderDao.findInvoicedOrdersInRange(startDate, endDate);
    }

    public OrderPojo update(Integer id, String name) throws ApiException {
        OrderPojo orderPojo = getCheckCreatedOrder(id);

        orderPojo.setName(name);

        return orderPojo;
    }

    public OrderPojo invoiceOrder(Integer id) throws ApiException {
        OrderPojo orderPojo = getCheck(id);

        orderPojo.setStatus(OrderStatus.INVOICED);

        return orderPojo;
    }

    private OrderPojo createOrder(String name) {
        OrderPojo orderPojo = new OrderPojo();

        orderPojo.setName(name);
        orderPojo.setStatus(OrderStatus.CREATED);

        return orderPojo;
    }

}
