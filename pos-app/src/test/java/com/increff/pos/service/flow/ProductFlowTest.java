package com.increff.pos.service.flow;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;

public class ProductFlowTest extends AbstractUnitTest {

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    // Tests adding product
    @Test
    public void testAdd1() throws ApiException {
        ProductPojo productPojo = apiTestUtil.createProduct("barcode", 1, "name", 12.12);
        Integer brandCategory = 3;

        productFlow.add(productPojo, brandCategory);

        InventoryPojo inventoryPojo = inventoryDao.get(productPojo.getId());
        assertEquals((Integer)0, inventoryPojo.getQuantity());
        assertEquals(brandCategory, productPojo.getBrandCategory());
    }

    // Tests adding productList
    @Test
    public void testAdd2() throws ApiException {
        List<String> barcodeList = Arrays.asList("barcode1", "barcode2");
        List<ProductPojo> productPojoList = apiTestUtil.createProductList(barcodeList, 1, "name", 12.12);

        productFlow.addList(productPojoList);

        List<ProductPojo> productPojoList1 = productDao.getAll();
        assertEquals(2, productPojoList1.size());
        IntStream.range(0,2).forEach(id -> {
            assertEquals(productPojoList.get(id), productPojoList1.get(id));
        });

        List<InventoryPojo> inventoryPojoList = inventoryDao.getAll();
        assertEquals(2, inventoryPojoList.size());
        inventoryPojoList
                .stream()
                .forEach(inventoryPojo -> assertEquals((Integer)0, inventoryPojo.getQuantity()));
    }

}
