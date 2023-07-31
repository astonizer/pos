package com.increff.pos.dto;

import com.increff.pos.model.data.ProductData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.ProductForm;
import com.increff.pos.model.form.ProductUpdateForm;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.ProductService;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.flow.ProductFlow;
import com.increff.pos.util.ConversionUtil;
import com.increff.pos.util.ExceptionUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;
import javafx.util.Pair;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j
public class ProductDto {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductFlow productFlow;

    public ProductData add(ProductForm productForm) throws ApiException {
        NormalizeUtil.normalize(productForm);
        ValidateUtil.validateForm(productForm);

        ProductPojo productPojo = ConversionUtil.convert(
                productForm,
                ProductPojo.class
        );

        BrandPojo brandPojo = brandService.getCheck(productForm.getBrand(), productForm.getCategory());

        productFlow.add(productPojo, brandPojo.getId());

        return convertToData(productPojo, productForm.getBrand(), productForm.getCategory());
    }

    public List<ProductData> addList(List<ProductForm> productFormList) throws ApiException {
        NormalizeUtil.normalize(productFormList);
        ValidateUtil.validateForm(productFormList);

        checkDuplicates(productFormList);

        Map<Pair<String, String>, Integer> brandCategoryToIdMap = checkValidBrands(productFormList);

        List<ProductPojo> productPojoList = convertToPojoList(productFormList, brandCategoryToIdMap);

        return convertToDataList(productFlow.addList(productPojoList));
    }

    public ProductData getCheck(Integer id) throws ApiException {
        ProductPojo productPojo = productService.getCheck(id);

        BrandPojo brandPojo = brandService.getCheck(productPojo.getBrandCategory());

        return convertToData(productPojo, brandPojo.getBrand(), brandPojo.getCategory());
    }

    public PaginationData<ProductData> getPaginatedList(Integer draw, Integer start, Integer length) throws ApiException {
        List<ProductPojo> productPojoList = productService.getPaginatedList(start, length);

        Integer productCount = productService.getCount();

        List<ProductData> productDataList = convertToDataList(productPojoList);

        return new PaginationData<>(productDataList, draw, productCount, productCount);
    }

    public ProductData update(Integer id, ProductUpdateForm productUpdateForm) throws ApiException {
        NormalizeUtil.normalize(productUpdateForm);
        ValidateUtil.validateForm(productUpdateForm);

        ProductPojo productPojo = productService.update(id, productUpdateForm.getName(), productUpdateForm.getMrp());

        return ConversionUtil.convert(
                productPojo,
                ProductData.class
        );
    }

    private ProductPojo convertFormToPojo(ProductForm productForm, Integer brandCategory) throws ApiException {
        ProductPojo productPojo = ConversionUtil.convert(productForm, ProductPojo.class);

        productPojo.setBrandCategory(brandCategory);

        return productPojo;
    }

    private List<ProductPojo> convertToPojoList(List<ProductForm> productFormList, Map<Pair<String, String>, Integer> brandCategoryToIdMap) throws ApiException {
        List<ProductPojo> productPojoList = new ArrayList<>();

        for(ProductForm productForm : productFormList) {
            Pair<String, String> brandCategoryPair = new Pair<>(productForm.getBrand(), productForm.getCategory());

            ProductPojo productPojo = convertFormToPojo(productForm, brandCategoryToIdMap.get(brandCategoryPair));

            productPojoList.add(productPojo);
        }

        return productPojoList;
    }

    private ProductData convertToData(ProductPojo productPojo, String brand, String category) throws ApiException {
        ProductData productData = ConversionUtil.convert(productPojo, ProductData.class);

        productData.setBrand(brand);
        productData.setCategory(category);

        return productData;
    }

    private List<ProductData> convertToDataList(List<ProductPojo> productPojoList) throws ApiException {
        List<ProductData> productDataList = new ArrayList<>();

        Map<Integer, BrandPojo> brandIdToPojoMap = getBrandIdToPojoMap(convertPojoListToBrandPojoList(productPojoList));

        for(ProductPojo productPojo: productPojoList) {
            BrandPojo brandPojo = brandIdToPojoMap.get(productPojo.getBrandCategory());
            ProductData productData = convertToData(productPojo, brandPojo.getBrand(), brandPojo.getCategory());

            productDataList.add(productData);
        }

        return productDataList;
    }

    private void checkDuplicates(List<ProductForm> productFormList) throws ApiException {
        Set<String> barcodeSet = new HashSet<>();

        ValidateUtil.validateList(productFormList, barcodeSet, (productForm,  set) -> {
            if (!set.add(productForm.getBarcode())) {
                return new Pair<>(true, "Duplicate entry: '" + productForm.getBarcode() + "'");
            }
            return new Pair<>(false, null);
        });
    }

    private Map<Pair<String, String>, Integer> checkValidBrands(List<ProductForm> productFormList) throws ApiException {
        Map<Pair<String, String>, Integer> brandCategoryToIdMap = getBrandCategoryToIdMap(this.convertFormListToBrandPojoList(productFormList));

        ValidateUtil.validateList(productFormList, brandCategoryToIdMap, (productForm, map) -> {
            if(map.get(new Pair<>(productForm.getBrand(), productForm.getCategory())) == null) {
                return new Pair<>(true, "Brand " + productForm.getBrand() + " - " + productForm.getCategory() + " doesn't exist");
            }
            return new Pair<>(false, null);
        });

        return brandCategoryToIdMap;
    }

    private Map<Pair<String, String>, Integer> getBrandCategoryToIdMap(List<BrandPojo> newBrandPojoList) {
        List<BrandPojo> brandPojoList = brandService.getByBrandListAndCategoryList(newBrandPojoList);

        return brandPojoList
                .stream()
                .collect(Collectors.toMap(
                        brandPojo -> new Pair<>(brandPojo.getBrand(), brandPojo.getCategory()),
                        BrandPojo::getId
                ));
    }

    private Map<Integer, BrandPojo> getBrandIdToPojoMap(List<BrandPojo> newBrandPojoList) {
        List<BrandPojo> brandPojoList = brandService.getByBrandListAndCategoryList(newBrandPojoList);

        return brandPojoList
                .stream()
                .collect(Collectors.toMap(BrandPojo::getId, brandPojo -> brandPojo));
    }

    private List<BrandPojo> convertFormListToBrandPojoList(List<ProductForm> productFormList) {
        return productFormList
                .stream()
                .map(productForm -> {
                    BrandPojo brandPojo = new BrandPojo();
                    brandPojo.setBrand(productForm.getBrand());
                    brandPojo.setCategory(productForm.getCategory());
                    return brandPojo;
                })
                .collect(Collectors.toList());
    }

    private List<BrandPojo> convertPojoListToBrandPojoList(List<ProductPojo> productPojoList) throws ApiException {
        List<BrandPojo> brandPojoList = new ArrayList<>();

        for(ProductPojo productPojo: productPojoList) {
            brandPojoList.add(brandService.getCheck(productPojo.getBrandCategory()));
        }

        return brandPojoList;
    }

}
