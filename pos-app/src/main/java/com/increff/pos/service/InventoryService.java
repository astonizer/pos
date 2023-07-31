package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.ValidateUtil;
import javafx.util.Pair;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j
public class InventoryService extends AbstractService {

    @Autowired
    private InventoryDao inventoryDao;

    public InventoryPojo addEmptyInventory(Integer productId) throws ApiException {
        InventoryPojo inventoryPojo = createEmptyInventory(productId);

        return inventoryDao.save(inventoryPojo);
    }

    public List<InventoryPojo> addEmptyInventory(List<Integer> productIdList) throws ApiException {
        List<InventoryPojo> inventoryPojoList = new ArrayList<>();

        for(Integer productId: productIdList) {
            inventoryPojoList.add(addEmptyInventory(productId));
        }

        return inventoryPojoList;
    }

    public List<InventoryPojo> getAll() {
        return inventoryDao.getAll();
    }

    public List<InventoryPojo> getPaginatedList(Integer start, Integer length) {
        return inventoryDao.getAllByPage(start, length);
    }

    public Integer getCount() {
        return inventoryDao.getCount();
    }

    public InventoryPojo increaseQuantity(Integer productId, Integer quantity) throws ApiException {
        InventoryPojo inventoryPojo = getCheck(productId);

        inventoryPojo.setQuantity(inventoryPojo.getQuantity() + quantity);

        return inventoryPojo;
    }

    public List<InventoryPojo> increaseQuantityOfList(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<InventoryPojo> newInventoryPojoList = new ArrayList<>();

        for(InventoryPojo inventoryPojo: inventoryPojoList) {
            newInventoryPojoList.add(increaseQuantity(inventoryPojo.getProductId(), inventoryPojo.getQuantity()));
        }

        return newInventoryPojoList;
    }

    public InventoryPojo decreaseQuantity(Integer productId, Integer quantity) throws ApiException {
        InventoryPojo inventoryPojo = getCheck(productId);

        if(inventoryPojo.getQuantity() < quantity) {
            throw new ApiException("Insufficient inventory");
        }

        inventoryPojo.setQuantity(inventoryPojo.getQuantity() - quantity);

        return inventoryPojo;
    }

    public List<InventoryPojo> decreaseQuantityOfList(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<InventoryPojo> newInventoryPojoList = new ArrayList<>();

        for(InventoryPojo inventoryPojo: inventoryPojoList) {
            newInventoryPojoList.add(decreaseQuantity(inventoryPojo.getProductId(), inventoryPojo.getQuantity()));
        }

        return newInventoryPojoList;
    }

    public InventoryPojo update(Integer productId, Integer quantity) throws ApiException {
        InventoryPojo inventoryPojo = getCheck(productId);

        inventoryPojo.setQuantity(quantity);

        return inventoryPojo;
    }

    public InventoryPojo getCheck(Integer productId) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.get(productId);

        checkNotNull(inventoryPojo, "Product doesn't exist");

        return inventoryPojo;
    }

    public void checkIfSufficientInventoryExists(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<InventoryPojo> existingInventoryPojoList = getExistingInventory(inventoryPojoList);

        Map<Integer, Integer> productIdToQuantityMap = existingInventoryPojoList
                .stream()
                .collect(Collectors.toMap(InventoryPojo::getProductId, InventoryPojo::getQuantity));

        ValidateUtil.validateList(inventoryPojoList, productIdToQuantityMap, (inventory, map) -> {
            if (map.get(inventory.getProductId()) < inventory.getQuantity()) {
                return new Pair<>(true, "Insufficient quantity");
            }
            return new Pair<>(false, null);
        });
    }

    private InventoryPojo createEmptyInventory(Integer productId) throws ApiException {
        checkIfInventoryExists(productId);

        InventoryPojo inventoryPojo = new InventoryPojo();

        inventoryPojo.setProductId(productId);
        inventoryPojo.setQuantity(0);

        return inventoryPojo;
    }

    private void checkIfInventoryExists(Integer productId) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.get(productId);

        checkNull(inventoryPojo, "Product already exists");
    }

    private List<InventoryPojo> getExistingInventory(List<InventoryPojo> inventoryPojoList) {
        List<Integer> productIdList = inventoryPojoList
                .stream()
                .map(InventoryPojo::getProductId)
                .collect(Collectors.toList());

        if(productIdList.isEmpty()) {
            return new ArrayList<>();
        }

        return inventoryDao.getByIdList(productIdList);
    }

}
