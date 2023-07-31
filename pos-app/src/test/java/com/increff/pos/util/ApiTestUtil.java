package com.increff.pos.util;

import com.increff.pos.dao.*;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.enums.UserRole;
import com.increff.pos.model.form.*;
import com.increff.pos.pojo.*;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.UserService;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ApiTestUtil {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    public BrandForm getBrandForm(String brand, String category) {
        BrandForm brandForm = new BrandForm();
        brandForm.setBrand(brand);
        brandForm.setCategory(category);
        return brandForm;
    }

    public List<BrandForm> getBrandFormList(List<String> brandList, List<String> categoryList) {
        return IntStream
                .range(0, brandList.size())
                .mapToObj(id -> getBrandForm(brandList.get(id), categoryList.get(id)))
                .collect(Collectors.toList());
    }

    public ProductForm getProductForm(String barcode, String brand, String category, String name, Double mrp) {
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setBrand(brand);
        productForm.setCategory(category);
        productForm.setName(name);
        productForm.setMrp(mrp);
        return productForm;
    }

    public ProductUpdateForm getProductUpdateForm(String name, Double mrp) {
        ProductUpdateForm productUpdateForm = new ProductUpdateForm();
        productUpdateForm.setName(name);
        productUpdateForm.setMrp(mrp);
        return productUpdateForm;
    }

    public List<ProductForm> getAddProductFormList(List<String> barcodeList, String brand, String category, String name, Double mrp) {
        return barcodeList
                .stream()
                .map(barcode -> getProductForm(barcode, brand, category, name, mrp))
                .collect(Collectors.toList());
    }

    public InventoryForm getInventoryForm(String barcode, Integer quantity) {
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

    public InventoryUpdateForm getInventoryUpdateForm(Integer quantity) {
        InventoryUpdateForm inventoryUpdateForm = new InventoryUpdateForm();
        inventoryUpdateForm.setQuantity(quantity);
        return inventoryUpdateForm;
    }

    public List<InventoryForm> getInventoryFormList(List<String> barcodeList, List<Integer> quantityList) {
        return IntStream
                .range(0, quantityList.size())
                .mapToObj(id -> getInventoryForm(barcodeList.get(id), quantityList.get(id)))
                .collect(Collectors.toList());
    }

    public OrderForm getOrderForm(String name, List<OrderItemForm> orderItemFormList) {
        OrderForm orderForm = new OrderForm();
        orderForm.setOrderItemFormList(orderItemFormList);
        orderForm.setName(name);
        return orderForm;
    }

    public OrderItemForm getOrderItemForm(String barcode, Integer quantity, Double sellingPrice) {
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setQuantity(quantity);
        orderItemForm.setBarcode(barcode);
        orderItemForm.setSellingPrice(sellingPrice);
        return orderItemForm;
    }

    public OrderItemUpdateForm getOrderItemUpdateForm(Integer orderId, String barcode, Integer quantity) {
        OrderItemUpdateForm orderItemUpdateForm = new OrderItemUpdateForm();
        orderItemUpdateForm.setOrderId(orderId);
        orderItemUpdateForm.setBarcode(barcode);
        orderItemUpdateForm.setQuantity(quantity);
        return orderItemUpdateForm;
    }

    public SalesReportForm getSalesReportForm(ZonedDateTime startDate, ZonedDateTime endDate) {
        SalesReportForm salesReportForm = new SalesReportForm();
        salesReportForm.setStartDate(startDate);
        salesReportForm.setEndDate(endDate);
        return salesReportForm;
    }

    public BrandPojo createBrand(String brand, String category) {
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brand);
        brandPojo.setCategory(category);
        return brandPojo;
    }

    public List<BrandPojo> createBrandList(List<String> brandList, List<String> categoryList) {
        return IntStream
                .range(0, brandList.size())
                .mapToObj(id -> {
                    BrandPojo brandPojo = new BrandPojo();
                    brandPojo.setCategory(categoryList.get(id));
                    brandPojo.setBrand(brandList.get(id));
                    return brandPojo;
                })
                .collect(Collectors.toList());
    }

    public ProductPojo createProduct(String barcode, Integer brandCategory , String name, Double mrp)  {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode(barcode);
        productPojo.setBrandCategory(brandCategory);
        productPojo.setName(name);
        productPojo.setMrp(mrp);
        return productPojo;
    }

    public List<ProductPojo> createProductList(List<String> barcodeList, Integer brandCategory, String name, Double mrp) {
        return barcodeList
                .stream()
                .map(barcode -> createProduct(barcode, brandCategory, name, mrp))
                .collect(Collectors.toList());
    }

    public InventoryPojo createInventory(Integer productId, Integer quantity) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(productId);
        inventoryPojo.setQuantity(quantity);
        return inventoryPojo;
    }

    public List<InventoryPojo> createInventoryList(List<Integer> productIdList, List<Integer> quantityList) {
        return IntStream
                .range(0, productIdList.size())
                .mapToObj(id -> createInventory(productIdList.get(id), quantityList.get(id)))
                .collect(Collectors.toList());
    }

    public OrderPojo createOrder(String name, OrderStatus orderStatus) {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setName(name);
        orderPojo.setStatus(orderStatus);
        return orderPojo;
    }

    public OrderPojo createOrder(String name, OrderStatus orderStatus, ZonedDateTime updatedAt) {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setName(name);
        orderPojo.setStatus(orderStatus);
        orderPojo.setUpdatedAt(updatedAt);
        return orderPojo;
    }

    public OrderItemPojo createOrderItem(Integer orderId, Integer productId, Integer quantity, Double sellingPrice) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setProductId(productId);
        orderItemPojo.setQuantity(quantity);
        orderItemPojo.setSellingPrice(sellingPrice);
        return orderItemPojo;
    }

    public List<OrderItemPojo> createOrderItemList(List<Integer> orderIdList, List<Integer> productIdList, Integer quantity, Double sellingPrice) {
        return IntStream
                .range(0, orderIdList.size())
                .mapToObj(id -> createOrderItem(orderIdList.get(id), productIdList.get(id), quantity, sellingPrice))
                .collect(Collectors.toList());
    }

    public UserPojo createUser(String email, String password, UserRole userRole) {
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail(email);
        userPojo.setPassword(password);
        userPojo.setRole(userRole);
        return userPojo;
    }

    public UserPojo addUser(String email, String password, UserRole userRole) {
        return userDao.save(createUser(email, password, userRole));
    }

    public BrandPojo addBrand(String brand, String category) {
        return brandDao.save(createBrand(brand, category));
    }

    public List<BrandPojo> addBrandList(List<String> brandList, List<String> categoryList) {
        return IntStream
                .range(0, brandList.size())
                .mapToObj(id -> addBrand(brandList.get(id), categoryList.get(id)))
                .collect(Collectors.toList());
    }

    public ProductPojo addProduct(String barcode, Integer brandCategory , String name, Double mrp)  {
        return productDao.save(createProduct(barcode, brandCategory, name, mrp));
    }

    public List<ProductPojo> addProductList(List<String> barcodeList, Integer brandCategory, String name, Double  mrp) {
        return IntStream
                .range(0, barcodeList.size())
                .mapToObj(id -> addProduct(barcodeList.get(id), brandCategory, name, mrp))
                .collect(Collectors.toList());
    }

    public List<ProductPojo> addProductList(List<String> barcodeList, List<Integer> brandCategoryList, String name, Double  mrp) {
        return IntStream
                .range(0, barcodeList.size())
                .mapToObj(id -> addProduct(barcodeList.get(id), brandCategoryList.get(id), name, mrp))
                .collect(Collectors.toList());
    }

    public InventoryPojo addInventory(Integer productId, Integer quantity) {
        return inventoryDao.save(createInventory(productId, quantity));
    }

    public List<InventoryPojo> addInventoryList(List<Integer> productId, Integer quantity) {
        return IntStream
                .range(0, productId.size())
                .mapToObj(id -> addInventory(productId.get(id), quantity))
                .collect(Collectors.toList());
    }

    public List<InventoryPojo> addInventoryList(List<Integer> productIdList, List<Integer> quantityList) {
        return IntStream
                .range(0, productIdList.size())
                .mapToObj(id -> addInventory(productIdList.get(id), quantityList.get(id)))
                .collect(Collectors.toList());
    }

    public InventoryPojo addEmptyInventory(Integer productId) {
        return inventoryDao.save(createInventory(productId, 0));
    }

    public OrderPojo addOrder(String name, OrderStatus orderStatus) {
        return orderDao.save(createOrder(name, orderStatus));
    }

    public OrderPojo addOrder(String name, OrderStatus orderStatus, ZonedDateTime updatedAt) {
        return orderDao.save(createOrder(name, orderStatus, updatedAt));
    }

    public OrderItemPojo addOrderItem(Integer orderId, Integer productId, Integer quantity, Double sellingPrice) {
        return orderItemDao.save(createOrderItem(orderId, productId, quantity, sellingPrice));
    }

    public List<OrderItemPojo> addOrderItemList(List<Integer> orderIdList, List<Integer> productIdList, Integer quantity, Double sellingPrice) {
        return IntStream
                .range(0, orderIdList.size())
                .mapToObj(id -> addOrderItem(orderIdList.get(id), productIdList.get(id), quantity, sellingPrice))
                .collect(Collectors.toList());
    }

    public DailySalesReportPojo addDailySalesReport(Integer invoicedItemsCount, Double totalRevenue, Integer invoicedOrdersCount) {
        DailySalesReportPojo dailySalesReportPojo = new DailySalesReportPojo();
        dailySalesReportPojo.setInvoicedItemsCount(invoicedItemsCount);
        dailySalesReportPojo.setTotalRevenue(totalRevenue);
        dailySalesReportPojo.setInvoicedOrdersCount(invoicedOrdersCount);
        reportDao.save(dailySalesReportPojo);
        return dailySalesReportPojo;
    }

    public OrderPojo placeOrder(Integer id) throws ApiException {
        return orderService.invoiceOrder(id);
    }

    public List<ProductPojo> getAllProducts() {
        return productDao.getAll();
    }

    public List<InventoryPojo> getAllInventory() {
        return inventoryDao.getAll();
    }

    public List<OrderPojo> getAllOrders() {
        return orderDao.getAll();
    }

    public List<OrderItemPojo> getAllOrderItems(Integer orderId) {
        return orderItemDao.getAllBySingleColumn("orderId", orderId);
    }

}
