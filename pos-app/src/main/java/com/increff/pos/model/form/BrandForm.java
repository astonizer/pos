package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class BrandForm {

    @Size(min = 1, max = 100, message = "Invalid brand length")
    @NotNull(message = "Null brand name")
    private String brand;

    @Size(min = 1, max = 100, message = "Invalid category length")
    @NotNull(message = "Null category name")
    private String category;

}
