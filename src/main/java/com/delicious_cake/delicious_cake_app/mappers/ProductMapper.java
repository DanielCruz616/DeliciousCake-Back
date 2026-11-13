package com.delicious_cake.delicious_cake_app.mappers;

import com.delicious_cake.delicious_cake_app.entities.ProductEntity;
import com.delicious_cake.delicious_cake_app.dtos.ProductDTO;

public class ProductMapper {

    public static ProductDTO toDTO(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());

        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
        }
        return dto;
    }

    public static ProductEntity toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(entity.getImageUrl());

        return entity;
    }
}