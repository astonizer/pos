package com.increff.pos.util;

import com.increff.pos.dao.*;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DaoUtil {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    public BrandPojo addBrand(String brand, String category) {
        BrandPojo brandPojo = createBrand(brand, category);
        brandDao.save(brandPojo);
        return brandPojo;
    }

    public List<BrandPojo> addBrandList(List<String> brandList, List<String> categoryList) {
        List<BrandPojo> brandPojoList = createBrandList(brandList, categoryList);
        for(BrandPojo brandPojo: brandPojoList) {
            brandDao.save(brandPojo);
        }
        return brandPojoList;
    }

    public ProductPojo addProduct(String barcode, Integer brandCategory, String name, Double mrp) {
        ProductPojo productPojo = createProduct(barcode, brandCategory, name, mrp);
        productDao.save(productPojo);
        return productPojo;
    }

    public List<ProductPojo> addProductList(List<ProductPojo> productPojoList) {
        return productPojoList.stream().map(productDao::save).collect(Collectors.toList());
    }

    public InventoryPojo addInventory(Integer productId, Integer quantity) {
        InventoryPojo inventoryPojo = createInventory(productId, quantity);
        inventoryDao.save(inventoryPojo);
        return inventoryPojo;
    }

    public OrderPojo addOrder(String name, OrderStatus status) {
        OrderPojo orderPojo = createOrder(name, status);
        orderDao.save(orderPojo);
        return orderPojo;
    }

    public OrderPojo addOrder(String name, OrderStatus status, ZonedDateTime updatedAt) {
        OrderPojo orderPojo = createOrder(name, status);
        orderPojo.setUpdatedAt(updatedAt);
        orderDao.save(orderPojo);
        return orderPojo;
    }

    public BrandPojo createBrand(String brand, String category) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brand);
        brandPojo.setCategory(category);
        return brandPojo;
    }

    public List<BrandPojo> createBrandList(List<String> brandList, List<String> categoryList) {
        return IntStream.range(0, brandList.size())
                .mapToObj(id -> createBrand(brandList.get(id), categoryList.get(id)))
                .collect(Collectors.toList());
    }

    public ProductPojo createProduct(String barcode, Integer brandCategory, String name, Double mrp) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode(barcode);
        productPojo.setBrandCategory(brandCategory);
        productPojo.setName(name);
        productPojo.setMrp(mrp);
        return productPojo;
    }

    public InventoryPojo createInventory(Integer productId, Integer quantity) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productId);
        inventoryPojo.setQuantity(quantity);
        return inventoryPojo;
    }

    public OrderPojo createOrder(String name, OrderStatus status) {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setName(name);
        orderPojo.setStatus(status);
        return orderPojo;
    }

    public List<OrderPojo> getAllOrders() {
        return orderDao.getAll();
    }

    public List<OrderItemPojo> getAllOrderItems(Integer orderId) {
        return orderItemDao.getAllBySingleColumn("orderId", orderId);
    }

}
