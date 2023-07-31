package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemForm {

    private String name;
    private Integer quantity;
    private Double sellingPrice;

}
