package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class InventoryUpdateForm {

    @Min(value = 0, message = "Quantity cannot be negative")
    @NotNull(message = "Null quantity value")
    @Digits(integer = 8, fraction = 0, message = "Quantity must be an integer")
    private Integer quantity;

}
