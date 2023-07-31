package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProductForm {

    @Size(min = 1, max = 100, message = "Invalid barcode length")
    @NotEmpty(message = "Empty barcode")
    private String barcode;

    @Size(min = 1, max = 100, message = "Invalid brand length")
    @NotEmpty(message = "Empty brand")
    private String brand;

    @Size(min = 1, max = 100, message = "Invalid category length")
    @NotEmpty(message = "Empty category")
    private String category;

    @Size(min = 1, max = 200, message = "Invalid name length")
    @NotEmpty(message = "Empty product name")
    private String name;

    @NotNull(message = "Empty MRP value")
    @Min(value = 0, message = "MRP must be greater than or equal to 0")
    private Double mrp;

}
