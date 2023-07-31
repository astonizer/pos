package com.increff.pos.controller.api;

import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.constants.PaginationConstants;
import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.ProductUpdateForm;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private ProductDto productDto;

    @ApiOperation(value = "Gets a product by id")
    @GetMapping("/{id}")
    public ProductData get(@PathVariable Integer id) throws ApiException {
        return productDto.getCheck(id);
    }

    @ApiOperation(value = "Gets list of all products")
    @GetMapping("")
    public PaginationData<ProductData> getAll(
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_DRAW) Integer draw,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_START) Integer start,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_LENGTH) Integer length
    ) throws ApiException {
        return productDto.getPaginatedList(draw, start, length);
    }

    @ApiOperation(value = "Adds a product")
    @PostMapping("")
    public ProductData add(@RequestBody ProductForm productForm) throws ApiException {
        return productDto.add(productForm);
    }

    @ApiOperation(value = "Adds a list of products")
    @PostMapping("/bulk")
    public List<ProductData> addList(@RequestBody List<ProductForm> productFormList) throws ApiException {
        return productDto.addList(productFormList);
    }

    @ApiOperation(value = "Updates a product")
    @PutMapping("/{id}")
    public ProductData update(@PathVariable Integer id, @RequestBody ProductUpdateForm productUpdateForm) throws ApiException {
        return productDto.update(id, productUpdateForm);
    }

}
