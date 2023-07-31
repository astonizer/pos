package com.increff.pos.service.flow;

import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class OrderItemFlow {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo update(Integer id, Integer productId, Integer newQuantity, Double sellingPrice) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemService.getCheck(id);
        Integer oldQuantity = orderItemPojo.getQuantity();

        orderService.getCheckCreatedOrder(orderItemPojo.getOrderId());

        InventoryPojo inventoryPojo = inventoryService.getCheck(productId);
        Integer existingQuantity = inventoryPojo.getQuantity();

        if(existingQuantity < newQuantity - oldQuantity) {
            throw new ApiException("Insufficient inventory for this product.");
        }

        inventoryPojo.setQuantity(existingQuantity - newQuantity + oldQuantity);
        orderItemPojo.setQuantity(newQuantity);

        orderItemPojo.setSellingPrice(sellingPrice);

        return orderItemPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo delete(Integer id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemService.getCheck(id);

        orderService.getCheckCreatedOrder(orderItemPojo.getOrderId());

        inventoryService.increaseQuantity(
                orderItemPojo.getProductId(),
                orderItemPojo.getQuantity()
        );

        orderItemPojo.setQuantity(0);

        return orderItemPojo;
    }

}
