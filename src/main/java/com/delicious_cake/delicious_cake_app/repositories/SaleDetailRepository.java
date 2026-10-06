package com.delicious_cake.delicious_cake_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delicious_cake.delicious_cake_app.entities.SaleDetailEntity;

public interface SaleDetailRepository extends JpaRepository<SaleDetailEntity, Long> {
    List<SaleDetailEntity> findBySaleId(Long saleId);
}
