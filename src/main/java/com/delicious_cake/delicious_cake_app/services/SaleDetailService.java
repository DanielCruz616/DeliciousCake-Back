package com.delicious_cake.delicious_cake_app.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delicious_cake.delicious_cake_app.dtos.SaleDetailDTO;
import com.delicious_cake.delicious_cake_app.dtos.SaleDetailRequestDTO;
import com.delicious_cake.delicious_cake_app.entities.ProductEntity;
import com.delicious_cake.delicious_cake_app.entities.SaleDetailEntity;
import com.delicious_cake.delicious_cake_app.entities.SaleEntity;
import com.delicious_cake.delicious_cake_app.mappers.SaleDetailMapper;
import com.delicious_cake.delicious_cake_app.repositories.ProductRepository;
import com.delicious_cake.delicious_cake_app.repositories.SaleDetailRepository;

@Service
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;

    public SaleDetailService(
            SaleDetailRepository saleDetailRepository,
            ProductRepository productRepository) {

        this.saleDetailRepository = saleDetailRepository;
        this.productRepository = productRepository;
    }

    // Replace all sale details
    @Transactional
    public List<SaleDetailDTO> replaceDetails(
            SaleEntity sale,
            List<SaleDetailRequestDTO> details) {

        // 1. Remove existing details.
        // orphanRemoval = true will delete them from the database.
        sale.getDetails().clear();

        BigDecimal total = BigDecimal.ZERO;

        List<SaleDetailEntity> newDetails = new ArrayList<>();

        // 2. Create the new details.
        for (SaleDetailRequestDTO detail : details) {

            validateSaleDetail(detail);

            ProductEntity product = productRepository.findById(
                    detail.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));

            BigDecimal subtotal = product.getPrice().multiply(
                    BigDecimal.valueOf(detail.getQuantity()));

            SaleDetailEntity entity = new SaleDetailEntity();

            entity.setSale(sale);
            entity.setProduct(product);
            entity.setQuantity(detail.getQuantity());
            entity.setUnitPrice(product.getPrice());
            entity.setSubtotal(subtotal);

            // Maintain the relationship on the SaleEntity side.
            sale.getDetails().add(entity);

            newDetails.add(entity);

            total = total.add(subtotal);
        }

        // 3. Save the new details.
        List<SaleDetailEntity> savedDetails = saleDetailRepository.saveAll(newDetails);

        // 4. Update sale total.
        sale.setTotal(total);

        return savedDetails.stream()
                .map(SaleDetailMapper::toDTO)
                .toList();
    }

    // Get by ID
    public SaleDetailDTO getById(Long id) {

        SaleDetailEntity detail = saleDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sale detail not found with id: " + id));

        return SaleDetailMapper.toDTO(detail);
    }

    // Get all
    public List<SaleDetailDTO> getAll() {

        return saleDetailRepository.findAll()
                .stream()
                .map(SaleDetailMapper::toDTO)
                .toList();
    }

    // Validation
    private void validateSaleDetail(SaleDetailRequestDTO dto) {

        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }
    }
}