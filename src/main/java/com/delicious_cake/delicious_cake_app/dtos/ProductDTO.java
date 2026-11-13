package com.delicious_cake.delicious_cake_app.dtos;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Long categoryId;
}