package com.increff.pos.controller.api;

import com.increff.pos.dto.BrandDto;
import com.increff.pos.model.constants.PaginationConstants;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/brands")
public class BrandApiController {

    @Autowired
    private BrandDto brandDto;

    @ApiOperation(value = "Adds a brand")
    @PostMapping("")
    public BrandData add(@RequestBody BrandForm brandForm) throws ApiException {
        return brandDto.add(brandForm);
    }

    @ApiOperation(value = "Adds a list of brands")
    @PostMapping("/bulk")
    public List<BrandData> addList(@RequestBody List<BrandForm> brandFormList) throws ApiException {
        return brandDto.addList(brandFormList);
    }

    @ApiOperation(value = "Gets a brand by id")
    @GetMapping("/{id}")
    public BrandData get(@PathVariable Integer id) throws ApiException {
        return brandDto.getCheck(id);
    }

    @ApiOperation(value = "Gets list of all brands")
    @GetMapping("")
    public PaginationData<BrandData> getAll(
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_DRAW) Integer draw,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_START) Integer start,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_LENGTH) Integer length
    ) throws ApiException {
        return brandDto.getPaginatedList(draw, start, length);
    }

    @ApiOperation(value = "Updates a brand")
    @PutMapping("/{id}")
    public BrandData update(@PathVariable Integer id, @RequestBody BrandForm brandForm) throws ApiException {
        return brandDto.update(id, brandForm);
    }

}
