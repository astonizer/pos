package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class OrderUpdateForm {

    @Size(max = 100, message = "Order name cannot exceed 100 characters")
    @NotEmpty(message = "Empty order name")
    private String name;

}
