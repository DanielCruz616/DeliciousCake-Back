package com.delicious_cake.delicious_cake_app.mappers;

import com.delicious_cake.delicious_cake_app.entities.SaleDetailEntity;
import com.delicious_cake.delicious_cake_app.dtos.SaleDetailDTO;

public class SaleDetailMapper {

    public static SaleDetailDTO toDTO(SaleDetailEntity entity) {
        if (entity == null) {
            return null;
        }

        SaleDetailDTO dto = new SaleDetailDTO();

        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setSubtotal(entity.getSubtotal());

        if (entity.getSale() != null) {
            dto.setSaleId(entity.getSale().getId());
        }

        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
            dto.setProductName(entity.getProduct().getName());
        }

        return dto;
    }

    public static SaleDetailEntity toEntity(SaleDetailDTO dto) {
        if (dto == null) {
            return null;
        }

        SaleDetailEntity entity = new SaleDetailEntity();

        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setSubtotal(dto.getSubtotal());

        return entity;
    }
}