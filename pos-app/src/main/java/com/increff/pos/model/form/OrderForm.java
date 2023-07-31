package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class OrderForm {

    @Size(max = 100, message = "Order name cannot exceed 100 characters")
    @NotEmpty(message = "Empty order name")
    private String name;

    @NotNull(message = "Empty order cannot be null")
    @Size(min = 1, message = "Order item list must have at least one item")
    private List<OrderItemForm> orderItemFormList;

}
