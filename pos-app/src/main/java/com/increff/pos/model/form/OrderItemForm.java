package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class OrderItemForm {

    @NotEmpty(message = "Empty barcode")
    @Size(max = 100, message = "Barcode length cannot exceed 100 characters")
    private String barcode;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be a positive number")
    @Digits(integer = 6, fraction = 0, message = "Quantity must be an integer")
    private Integer quantity;

    @NotNull(message = "Null selling price value")
    @Min(value = 0, message = "Selling price must be greater than or equal to 0")
    private Double sellingPrice;

}
