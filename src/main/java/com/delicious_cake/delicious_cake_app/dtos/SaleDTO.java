package com.delicious_cake.delicious_cake_app.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.delicious_cake.delicious_cake_app.enums.SaleStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleDTO {

    private Long id;
    private Long customerId;
    private Long tableId;
    private LocalDate createdAt;
    private BigDecimal total;
    private SaleStatus status;
    private List<SaleDetailDTO> details = new ArrayList<>();
}