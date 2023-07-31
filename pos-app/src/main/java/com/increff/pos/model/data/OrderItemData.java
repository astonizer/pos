package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderItemForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemData extends OrderItemForm {

    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Double total;

}
