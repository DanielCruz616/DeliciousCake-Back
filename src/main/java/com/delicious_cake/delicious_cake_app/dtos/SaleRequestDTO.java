package com.delicious_cake.delicious_cake_app.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class SaleRequestDTO {

    private Long customerId;
    private Long tableId;

    private List<SaleDetailRequestDTO> details = new ArrayList<>();
}
