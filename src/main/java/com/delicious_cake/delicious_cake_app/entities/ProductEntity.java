package com.delicious_cake.delicious_cake_app.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "products")
public class ProductEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @OneToOne(mappedBy = "product")
    private InventoryEntity inventory;

    @OneToMany(mappedBy = "product")
    private List<InventoryMovementEntity> inventoryMovements = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<SaleDetailEntity> saleDetails = new ArrayList<>();
}