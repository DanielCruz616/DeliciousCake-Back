package com.delicious_cake.delicious_cake_app.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delicious_cake.delicious_cake_app.dtos.SaleDetailDTO;
import com.delicious_cake.delicious_cake_app.entities.ProductEntity;
import com.delicious_cake.delicious_cake_app.entities.SaleDetailEntity;
import com.delicious_cake.delicious_cake_app.entities.SaleEntity;
import com.delicious_cake.delicious_cake_app.enums.SaleStatus;
import com.delicious_cake.delicious_cake_app.mappers.SaleDetailMapper;
import com.delicious_cake.delicious_cake_app.repositories.ProductRepository;
import com.delicious_cake.delicious_cake_app.repositories.SaleDetailRepository;
import com.delicious_cake.delicious_cake_app.repositories.SaleRepository;

@Service
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public SaleDetailService(
            SaleDetailRepository saleDetailRepository,
            SaleRepository saleRepository,
            ProductRepository productRepository) {

        this.saleDetailRepository = saleDetailRepository;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    //Create Method
    @Transactional
    public SaleDetailDTO create(SaleDetailDTO dto) {

        validateSaleDetail(dto);

        SaleEntity sale = saleRepository.findById(dto.getSaleId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Sale not found with id: " + dto.getSaleId()));

        validateSaleIsOpen(sale);

        SaleDetailEntity saleDetail = SaleDetailMapper.toEntity(dto);

        ProductEntity product = productRepository.findById(dto.getProductId()).orElseThrow(() ->
                new IllegalArgumentException("Product not found with id: " + dto.getProductId()));

        BigDecimal unitPrice = product.getPrice();

        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

        saleDetail.setSale(sale);
        saleDetail.setProduct(product);
        saleDetail.setUnitPrice(unitPrice);
        saleDetail.setSubtotal(subtotal);

        SaleDetailEntity savedSaleDetail = saleDetailRepository.save(saleDetail);

        updateSaleTotal(sale);

        return SaleDetailMapper.toDTO(savedSaleDetail);
    }

    //Get Method
    public SaleDetailDTO getById(Long id) {

        SaleDetailEntity saleDetail =
                saleDetailRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sale detail not found with id: " + id));

        return SaleDetailMapper.toDTO(saleDetail);
    }

    //Get All Method
    public List<SaleDetailDTO> getAll() {

        return saleDetailRepository.findAll()
                .stream()
                .map(SaleDetailMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Update Method
    @Transactional
    public SaleDetailDTO update(Long id, SaleDetailDTO dto) {

        SaleDetailEntity existingSaleDetail =
                saleDetailRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sale detail not found with id: " + id));

        validateSaleDetail(dto);

        SaleEntity sale = existingSaleDetail.getSale();

        validateSaleIsOpen(sale);

        ProductEntity product =
                productRepository.findById(dto.getProductId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found with id: "
                                                + dto.getProductId()));

        BigDecimal unitPrice = product.getPrice();

        BigDecimal subtotal = unitPrice.multiply(
                BigDecimal.valueOf(dto.getQuantity())
        );

        existingSaleDetail.setProduct(product);
        existingSaleDetail.setQuantity(dto.getQuantity());
        existingSaleDetail.setUnitPrice(unitPrice);
        existingSaleDetail.setSubtotal(subtotal);

        SaleDetailEntity updatedSaleDetail = saleDetailRepository.save(existingSaleDetail);

        updateSaleTotal(sale);

        return SaleDetailMapper.toDTO(updatedSaleDetail);
    }

    // Delete Method
    @Transactional
    public void delete(Long id) {

        SaleDetailEntity saleDetail =
                saleDetailRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sale detail not found with id: " + id));

        SaleEntity sale = saleDetail.getSale();

        validateSaleIsOpen(sale);

        saleDetailRepository.delete(saleDetail);

        updateSaleTotal(sale);
    }

    // Validation Method
    private void validateSaleDetail(SaleDetailDTO dto) {

        if (dto.getSaleId() == null) {
            throw new IllegalArgumentException(
                    "Sale ID cannot be null");
        }

        if (dto.getProductId() == null) {
            throw new IllegalArgumentException(
                    "Product ID cannot be null");
        }

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }
    }

    private void validateSaleIsOpen(SaleEntity sale) {

        if (sale.getStatus() != SaleStatus.OPEN) {
            throw new IllegalStateException("Sale cannot be modified because its status is " + sale.getStatus());
        }
    }

    private void updateSaleTotal(SaleEntity sale) {

        BigDecimal total = saleDetailRepository
                .findBySaleId(sale.getId())
                .stream()
                .map(SaleDetailEntity::getSubtotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        sale.setTotal(total);

        saleRepository.save(sale);
    }
}