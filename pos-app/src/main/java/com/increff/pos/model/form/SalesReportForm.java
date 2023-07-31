package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesReportForm {

    @NotNull(message = "Null start date")
    private ZonedDateTime startDate;

    @NotNull(message = "Null end date")
    private ZonedDateTime endDate;

    private String brand;

    private String category;

}
