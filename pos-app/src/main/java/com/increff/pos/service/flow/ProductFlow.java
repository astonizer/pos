package com.increff.pos.service.flow;

import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductFlow {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo add(ProductPojo productPojo, Integer brandCategory) throws ApiException {
        productService.add(productPojo, brandCategory);

        inventoryService.addEmptyInventory(productPojo.getId());

        return productPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<ProductPojo> addList(List<ProductPojo> productPojoList) throws ApiException {
        productService.addList(productPojoList);

        List<Integer> productIdList = productPojoList
                .stream()
                .map(ProductPojo::getId)
                .collect(Collectors.toList());

        inventoryService.addEmptyInventory(productIdList);

        return productPojoList;
    }

}
