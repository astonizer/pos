package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProductUpdateForm {

    @Size(min = 1, max = 200, message = "Invalid name length")
    @NotNull(message = "Null product name")
    private String name;

    @NotNull(message = "Null MRP value")
    @Min(value = 0, message = "MRP must be greater than or equal to 0")
    private Double mrp;

}
