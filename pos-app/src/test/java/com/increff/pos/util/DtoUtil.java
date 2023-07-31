package com.increff.pos.util;

import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DtoUtil {

    public OrderForm createOrderForm(String name, List<OrderItemForm> orderItemFormList) {
        OrderForm orderForm = new OrderForm();
        orderForm.setName(name);
        orderForm.setOrderItemFormList(orderItemFormList);
        return orderForm;
    }

    public OrderItemForm createOrderItemForm(String barcode, Integer quantity, Double sellingPrice) {
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setBarcode(barcode);
        orderItemForm.setQuantity(quantity);
        orderItemForm.setSellingPrice(sellingPrice);
        return orderItemForm;
    }

}
