package com.increff.pos.dto;

import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderItemUpdateForm;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.service.flow.OrderItemFlow;
import com.increff.pos.util.ConversionUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemDto {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemFlow orderItemFlow;

    public OrderItemData update(Integer id, OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        NormalizeUtil.normalize(orderItemUpdateForm);
        ValidateUtil.validateForm(orderItemUpdateForm);

        orderService.getCheckCreatedOrder(orderItemUpdateForm.getOrderId());

        ProductPojo productPojo = productService.getCheck(orderItemUpdateForm.getBarcode());

        return ConversionUtil.convert(
                orderItemFlow.update(
                        id, productPojo.getId(), orderItemUpdateForm.getQuantity(), orderItemUpdateForm.getSellingPrice()
                ),
                OrderItemData.class
        );
    }

    public OrderItemPojo delete(Integer id) throws ApiException {
        return orderItemFlow.delete(id);
    }

}
