package com.increff.pos.dto;

import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.InventoryUpdateForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.model.data.InventoryData;
import com.increff.pos.model.form.InventoryForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.ConversionUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InventoryDto {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    public InventoryData increaseQuantity(InventoryForm inventoryForm) throws ApiException {
        NormalizeUtil.normalize(inventoryForm);
        ValidateUtil.validateForm(inventoryForm);

        ProductPojo productPojo = productService.getCheck(inventoryForm.getBarcode());

        InventoryPojo inventoryPojo = inventoryService.increaseQuantity(
                productPojo.getId(),
                inventoryForm.getQuantity()
        );

        return convertToData(inventoryPojo, inventoryForm.getBarcode());
    }

    public List<InventoryData> increaseQuantityOfList(List<InventoryForm> inventoryFormList) throws ApiException {
        NormalizeUtil.normalize(inventoryFormList);
        ValidateUtil.validateForm(inventoryFormList);

        List<String> barcodeList = inventoryFormList
                .stream()
                .map(InventoryForm::getBarcode)
                .collect(Collectors.toList());

        Map<String, Integer> barcodeToProductIdMap = getBarcodeToProductIdMap(barcodeList);

        List<InventoryPojo> inventoryPojoList = inventoryService
                .increaseQuantityOfList(
                        convertToPojo(inventoryFormList, barcodeToProductIdMap)
                );

        return convertToDataList(inventoryPojoList);
    }

    public InventoryData getCheck(Integer productId) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.getCheck(productId);

        ProductPojo productPojo = productService.getCheck(productId);

        return convertToData(inventoryPojo, productPojo.getBarcode());
    }

    public PaginationData<InventoryData> getPaginatedList(Integer draw, Integer start, Integer length) throws ApiException {
        List<InventoryPojo> inventoryPojoList = inventoryService.getPaginatedList(start, length);

        Integer inventoryCount = inventoryService.getCount();

        List<InventoryData> inventoryDataList = convertToDataList(inventoryPojoList);

        return new PaginationData<>(inventoryDataList, draw, inventoryCount, inventoryCount);
    }

    public InventoryData updateQuantity(Integer productId, InventoryUpdateForm inventoryUpdateForm) throws ApiException {
        NormalizeUtil.normalize(inventoryUpdateForm);
        ValidateUtil.validateForm(inventoryUpdateForm);

        return ConversionUtil.convert(
                inventoryService.update(productId, inventoryUpdateForm.getQuantity()),
                InventoryData.class
        );
    }

    public InventoryData checkQuantity(InventoryForm inventoryForm) throws ApiException {
        NormalizeUtil.normalize(inventoryForm);
        ValidateUtil.validateForm(inventoryForm);

        ProductPojo productPojo = productService.getCheck(inventoryForm.getBarcode());

        InventoryPojo inventoryPojo = inventoryService.getCheck(productPojo.getId());

        if(inventoryPojo.getQuantity() == 0) {
            throw new ApiException("This item is currently out of stock.");
        }

        if(inventoryPojo.getQuantity() < inventoryForm.getQuantity()) {
            throw new ApiException("Required inventory for this order item is currently unavailable.");
        }

        return ConversionUtil.convert(
                inventoryPojo,
                InventoryData.class
        );
    }

    private InventoryData convertToData(InventoryPojo inventoryPojo, String barcode) throws ApiException {
        InventoryData inventoryData = ConversionUtil.convert(
                inventoryPojo,
                InventoryData.class
        );

        inventoryData.setBarcode(barcode);

        return inventoryData;
    }

    private List<InventoryData> convertToDataList(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<InventoryData> inventoryDataList = new ArrayList<>();

        List<Integer> productIdList = inventoryPojoList
                .stream()
                .map(InventoryPojo::getProductId)
                .collect(Collectors.toList());

        Map<Integer, String> productIdToBarcodeMap = getProductIdToBarcodeMap(productIdList);

        for(InventoryPojo inventoryPojo: inventoryPojoList) {
            InventoryData inventoryData = convertToData(
                    inventoryPojo,
                    productIdToBarcodeMap.get(inventoryPojo.getProductId())
            );
            inventoryDataList.add(inventoryData);
        }

        return inventoryDataList;
    }

    private List<InventoryPojo> convertToPojo(List<InventoryForm> inventoryFormList, Map<String, Integer> barcodeToProductIdMap) {
        return inventoryFormList.stream()
                .map(inventoryForm -> {
                    InventoryPojo inventoryPojo = new InventoryPojo();
                    inventoryPojo.setProductId(barcodeToProductIdMap.get(inventoryForm.getBarcode()));
                    inventoryPojo.setQuantity(inventoryForm.getQuantity());
                    return inventoryPojo;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getBarcodeToProductIdMap(List<String> barcodeList) throws ApiException {
        List<ProductPojo> productPojoList = productService.getCheckByBarcodeList(barcodeList);

        Map<String, Integer> barcodeToProductIdMap = productPojoList
                .stream()
                .collect(Collectors.toMap(ProductPojo::getBarcode, ProductPojo::getId));

        ValidateUtil.validateList(barcodeList, barcodeToProductIdMap, (barcode, map) -> {
            if(!map.containsKey(barcode)) {
                return new Pair<>(true, "No product registered with barcode '" + barcode + "' found");
            }
            return new Pair<>(false, null);
        });

        return barcodeToProductIdMap;
    }

    private Map<Integer, String> getProductIdToBarcodeMap(List<Integer> productIdList) {
        List<ProductPojo> productPojoList = productService.getAllByIdList(productIdList);

        return productPojoList
                .stream()
                .collect(Collectors.toMap(ProductPojo::getId, ProductPojo::getBarcode));
    }

}
