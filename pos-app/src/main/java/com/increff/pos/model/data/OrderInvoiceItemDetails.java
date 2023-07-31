package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInvoiceItemDetails {

    private String name;
    private Integer quantity;
    private Double sellingPrice;

}
