package com.increff.pos.dto;

import com.increff.pos.model.data.PaginationData;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.model.data.BrandData;
import com.increff.pos.model.form.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.ConversionUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;
import javafx.util.Pair;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j
public class BrandDto {

    @Autowired
    private BrandService brandService;

    public BrandData add(BrandForm brandForm) throws ApiException {
        NormalizeUtil.normalize(brandForm);
        ValidateUtil.validateForm(brandForm);

        BrandPojo brandPojo = ConversionUtil.convert(brandForm, BrandPojo.class);

        return ConversionUtil.convert(
                brandService.add(brandPojo),
                BrandData.class
        );
    }

    public List<BrandData> addList(List<BrandForm> brandFormList) throws ApiException {
        NormalizeUtil.normalize(brandFormList);
        ValidateUtil.validateForm(brandFormList);

        checkDuplicates(brandFormList);

        List<BrandPojo> brandPojoList = brandService.addList(
                ConversionUtil.convert(
                        brandFormList,
                        BrandPojo.class
                )
        );

        return ConversionUtil.convert(
                brandPojoList,
                BrandData.class
        );
    }

    public BrandData getCheck(Integer id) throws ApiException {
        BrandPojo brandPojo = brandService.getCheck(id);

        return ConversionUtil.convert(
                brandPojo,
                BrandData.class
        );
    }

    public PaginationData<BrandData> getPaginatedList(Integer draw, Integer start, Integer length) throws ApiException {
        List<BrandPojo> brandPojoList = brandService.getPaginatedList(start, length);

        Integer brandCount = brandService.getCount();

        List<BrandData> brandDataList = convertToDataList(brandPojoList);

        return new PaginationData<>(brandDataList, draw, brandCount, brandCount);
    }

    public BrandData update(Integer id, BrandForm brandForm) throws ApiException {
        NormalizeUtil.normalize(brandForm);
        ValidateUtil.validateForm(brandForm);

        BrandPojo brandPojo = brandService.update(id, brandForm.getBrand(), brandForm.getCategory());

        return ConversionUtil.convert(
                brandPojo,
                BrandData.class
        );
    }

    private List<BrandData> convertToDataList(List<BrandPojo> brandPojoList) throws ApiException {
        List<BrandData> brandDataList = new ArrayList<>();

        for(BrandPojo brandPojo: brandPojoList) {
            brandDataList.add(ConversionUtil.convert(
                    brandPojo,
                    BrandData.class
            ));
        }

        return brandDataList;
    }

    private void checkDuplicates(List<BrandForm> brandFormList) throws ApiException {
        Set<Pair<String, String>> brandCategorySet = new HashSet<>();

        ValidateUtil.validateList(brandFormList, brandCategorySet, (brandForm, set) -> {
            if (!set.add(new Pair<>(brandForm.getBrand(), brandForm.getCategory()))) {
                return new Pair<>(true, "Duplicate entry: " + brandForm.getBrand() + " - " + brandForm.getCategory());
            }
            return new Pair<>(false, null);
        });
    }

}
