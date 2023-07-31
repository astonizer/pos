package com.increff.pos.service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.ValidateUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService extends AbstractService {

    @Autowired
    private ProductDao productDao;

    public ProductPojo add(ProductPojo productPojo, Integer brandCategory) throws ApiException {
        checkIfBarcodeExists(productPojo.getBarcode());

        productPojo.setBrandCategory(brandCategory);

        return productDao.save(productPojo);
    }

    public List<ProductPojo> addList(List<ProductPojo> productPojoList) throws ApiException {
        validateList(productPojoList);

        return productPojoList.stream()
                .map(productDao::save)
                .collect(Collectors.toList());
    }

    public ProductPojo getCheck(Integer id) throws ApiException {
        ProductPojo productPojo = productDao.get(id);

        checkNotNull(productPojo, "Product doesn't exist");

        return productPojo;
    }

    public ProductPojo getCheck(String barcode) throws ApiException {
        ProductPojo productPojo = productDao.getBySingleColumn("barcode", barcode);

        checkNotNull(productPojo, "Product doesn't exist");

        return productPojo;
    }

    public List<ProductPojo> getCheckByBarcodeList(List<String> barcodeList) throws ApiException {
        List<ProductPojo> productPojoList = getAll(barcodeList);
        Set<String> barcodeSet = productPojoList.stream().map(ProductPojo::getBarcode).collect(Collectors.toSet());

        ValidateUtil.validateList(barcodeList, barcodeSet, (barcode, productBarcodeSet) -> {
            if(!productBarcodeSet.contains(barcode)) {
                return new Pair<>(true, "No product registered with barcode '" + barcode + "' found");
            }
            return new Pair<>(false, null);
        });

        return productPojoList;
    }

    public List<ProductPojo> getAll() {
        return productDao.getAll();
    }

    public List<ProductPojo> getPaginatedList(Integer start, Integer length) {
        return productDao.getAllByPage(start, length);
    }

    public Integer getCount() {
        return productDao.getCount();
    }

    public List<ProductPojo> getAllByIdList(List<Integer> idList) {
        if(idList.isEmpty()) {
            return new ArrayList<>();
        }
        return productDao.getAllByColumList(idList, "id");
    }

    public List<ProductPojo> getAllByBrandIdList(List<Integer> brandCategoryList) {
        if(brandCategoryList.isEmpty()) {
            return new ArrayList<>();
        }
        return productDao.getAllByColumList(brandCategoryList, "brandCategory");
    }

    public Map<String, Integer> getBarcodeToProductIdMap(List<String> barcodeList) {
        List<ProductPojo> productPojoList = getAll(barcodeList);

        return productPojoList
                .stream()
                .collect(Collectors.toMap(ProductPojo::getBarcode, ProductPojo::getId));
    }

    public ProductPojo update(Integer id, String name, Double mrp) throws ApiException {
        ProductPojo productPojo = getCheck(id);

        productPojo.setName(name);
        productPojo.setMrp(mrp);

        return productPojo;
    }

    private void validateList(List<ProductPojo> productPojoList) throws ApiException {
        Set<String> barcodeSet = getSetOfExistingBarcodes(productPojoList);

        ValidateUtil.validateList(productPojoList, barcodeSet, (product, productBarcodeSet) -> {
            if (!productBarcodeSet.add(product.getBarcode())) {
                return new Pair<>(true, "Duplicate entry: '" + product.getBarcode() + "'");
            }
            return new Pair<>(false, null);
        });
    }

    private Set<String> getSetOfExistingBarcodes(List<ProductPojo> newProductPojoList) {
        List<ProductPojo> productPojoList = getAll(
            newProductPojoList.stream()
                    .map(ProductPojo::getBarcode)
                    .collect(Collectors.toList())
        );

        return productPojoList.stream()
                .map(ProductPojo::getBarcode)
                .collect(Collectors.toSet());
    }

    private List<ProductPojo> getAll(List<String> barcodeList) {
        if(barcodeList.isEmpty()) {
            return new ArrayList<>();
        }
        return productDao.getAllByColumList(barcodeList, "barcode");
    }

    private void checkIfBarcodeExists(String barcode) throws ApiException {
        ProductPojo productPojo = productDao.getBySingleColumn("barcode", barcode);

        checkNull(productPojo, "Product with barcode '" + barcode + "' already exists");
    }

}
