package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class InventoryForm {

    @Size(min = 1, max = 100, message = "Invalid barcode length")
    @NotNull(message = "Null barcode value")
    private String barcode;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Null quantity value")
    @Digits(integer = 8, fraction = 0, message = "Quantity must be an integer smaller than 10^8")
    private Integer quantity;

}
