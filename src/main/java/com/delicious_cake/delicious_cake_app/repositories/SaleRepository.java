package com.delicious_cake.delicious_cake_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delicious_cake.delicious_cake_app.entities.SaleEntity;
import com.delicious_cake.delicious_cake_app.enums.SaleStatus;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
    List<SaleEntity> findByStatus(SaleStatus status);
}
