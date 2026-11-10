package com.delicious_cake.delicious_cake_app.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleDetailRequestDTO {
    
    private Long productId;
    private Integer quantity;
}