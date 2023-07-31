package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
public class OrderItemUpdateForm {

    @Digits(integer = 8, fraction = 0, message = "Quantity must be an integer")
    private Integer orderId;

    @Size(min = 1, max = 100, message = "Invalid barcode length")
    @NotNull(message = "Null barcode value")
    private String barcode;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Null quantity value")
    @Digits(integer = 8, fraction = 0, message = "Quantity must be an integer")
    private Integer quantity;

    @NotNull(message = "Null selling price value")
    @Min(value = 0, message = "Selling price must be greater than or equal to 0")
    private Double sellingPrice;

}
