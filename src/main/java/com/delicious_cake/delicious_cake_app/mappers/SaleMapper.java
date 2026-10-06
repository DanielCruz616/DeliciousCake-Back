package com.delicious_cake.delicious_cake_app.mappers;

import com.delicious_cake.delicious_cake_app.entities.SaleEntity;
import com.delicious_cake.delicious_cake_app.dtos.SaleDTO;

public class SaleMapper {

    public static SaleDTO toDTO(SaleEntity entity) {

        if (entity == null) {
            return null;
        }

        SaleDTO dto = new SaleDTO();

        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setTotal(entity.getTotal());
        dto.setStatus(entity.getStatus());

        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
        }

        if (entity.getTable() != null) {
            dto.setTableId(entity.getTable().getId());
        }

        if (entity.getDetails() != null) {
            dto.setDetails(
                    entity.getDetails()
                            .stream()
                            .map(SaleDetailMapper::toDTO)
                            .toList());
        }

        return dto;
    }

    public static SaleEntity toEntity(SaleDTO dto) {

        if (dto == null) {
            return null;
        }

        SaleEntity entity = new SaleEntity();

        entity.setCreatedAt(dto.getCreatedAt());
        entity.setTotal(dto.getTotal());
        entity.setStatus(dto.getStatus());

        return entity;
    }
}