package com.increff.pos.service;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemService extends AbstractService {
    
    @Autowired
    private OrderItemDao orderItemDao;

    public OrderItemPojo add(Integer orderId, OrderItemPojo orderItemPojo) throws ApiException {
        OrderItemPojo existingOrderItemPojo = orderItemDao.getByOrderIdAndProductId(orderId, orderItemPojo.getProductId());

        checkNull(existingOrderItemPojo, "Order item already exists");

        orderItemPojo.setOrderId(orderId);

        return orderItemDao.save(orderItemPojo);
    }

    public List<OrderItemPojo> add(List<OrderItemPojo> orderItemPojoList) {
        return orderItemPojoList
                .stream()
                .map(orderItemDao::save)
                .collect(Collectors.toList());
    }

    public OrderItemPojo update(Integer id, Integer quantity) {
        OrderItemPojo orderItemPojo = orderItemDao.get(id);

        orderItemPojo.setQuantity(quantity);

        return orderItemPojo;
    }

    public void deleteByOrderId(Integer orderId) {
        orderItemDao.deleteByColumn("orderId", orderId, OrderItemPojo.class);
    }

    public OrderItemPojo getCheck(Integer id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.get(id);

        checkNotNull(orderItemPojo, "Order item doesn't exist");

        return orderItemDao.get(id);
    }

    public List<OrderItemPojo> getOrderItemsByOrderId(Integer orderId) {
        return orderItemDao.getAllBySingleColumn("orderId", orderId);
    }

}
