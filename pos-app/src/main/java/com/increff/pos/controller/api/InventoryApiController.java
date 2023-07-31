package com.increff.pos.controller.api;

import com.increff.pos.dto.InventoryDto;
import com.increff.pos.model.constants.PaginationConstants;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.model.form.InventoryUpdateForm;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController {

    @Autowired
    private InventoryDto inventoryDto;

    @ApiOperation(value = "Gets an inventory detail by id")
    @GetMapping("/{productId}")
    public InventoryData get(@PathVariable Integer productId) throws ApiException {
        return inventoryDto.getCheck(productId);
    }

    @ApiOperation(value = "Gets list of all inventory's detail")
    @GetMapping("")
    public PaginationData<InventoryData> getAll(
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_DRAW) Integer draw,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_START) Integer start,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_LENGTH) Integer length
    ) throws ApiException {
        return inventoryDto.getPaginatedList(draw, start, length);
    }

    @ApiOperation(value = "Increases quantity of inventory")
    @PostMapping("")
    public InventoryData add(@RequestBody InventoryForm inventoryForm) throws ApiException {
        return inventoryDto.increaseQuantity(inventoryForm);
    }

    @ApiOperation(value = "Increases quantity of list of inventory")
    @PostMapping("/bulk")
    public List<InventoryData> addList(@RequestBody List<InventoryForm> inventoryFormList) throws ApiException {
        return inventoryDto.increaseQuantityOfList(inventoryFormList);
    }

    @ApiOperation(value = "Updates quantity of inventory")
    @PutMapping("/{productId}")
    public InventoryData update(@RequestBody InventoryUpdateForm inventoryUpdateForm, @PathVariable Integer productId) throws ApiException {
        return inventoryDto.updateQuantity(productId, inventoryUpdateForm);
    }

    @ApiOperation(value = "Checks quantity of inventory")
    @PostMapping("/check")
    public InventoryData check(@RequestBody InventoryForm inventoryForm) throws ApiException {
        return inventoryDto.checkQuantity(inventoryForm);
    }


}