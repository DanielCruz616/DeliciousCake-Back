package com.delicious_cake.delicious_cake_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delicious_cake.delicious_cake_app.dtos.SaleDTO;
import com.delicious_cake.delicious_cake_app.entities.CustomerEntity;
import com.delicious_cake.delicious_cake_app.entities.SaleEntity;
import com.delicious_cake.delicious_cake_app.entities.StandEntity;
import com.delicious_cake.delicious_cake_app.enums.SaleStatus;
import com.delicious_cake.delicious_cake_app.mappers.SaleMapper;
import com.delicious_cake.delicious_cake_app.repositories.CustomerRepository;
import com.delicious_cake.delicious_cake_app.repositories.SaleRepository;
import com.delicious_cake.delicious_cake_app.repositories.StandRepository;

@Service
public class SaleService {

        private final SaleRepository saleRepository;
        private final CustomerRepository customerRepository;
        private final StandRepository standRepository;

        public SaleService(
                        SaleRepository saleRepository,
                        CustomerRepository customerRepository,
                        StandRepository standRepository) {

                this.saleRepository = saleRepository;
                this.customerRepository = customerRepository;
                this.standRepository = standRepository;
        }

        // Create an empty OPEN sale
        @Transactional
        public SaleDTO create(SaleDTO dto) {

                validateSale(dto);

                SaleEntity sale = new SaleEntity();

                sale.setCustomer(
                                findCustomerById(dto.getCustomerId()));

                sale.setTable(
                                findStandById(dto.getTableId()));

                sale.setCreatedAt(LocalDate.now());

                sale.setTotal(BigDecimal.ZERO);

                sale.setStatus(SaleStatus.OPEN);

                SaleEntity savedSale = saleRepository.save(sale);

                return SaleMapper.toDTO(savedSale);
        }

        // Get by ID
        public SaleDTO getById(Long id) {

                SaleEntity sale = findSaleById(id);

                return SaleMapper.toDTO(sale);
        }

        // Get all
        public List<SaleDTO> getAll() {

                return saleRepository.findAll()
                                .stream()
                                .map(SaleMapper::toDTO)
                                .collect(Collectors.toList());
        }

        // Update customer/table
        @Transactional
        public SaleDTO update(Long id, SaleDTO dto) {

                SaleEntity existingSale = findSaleById(id);

                validateSaleIsOpen(existingSale);
                validateSale(dto);

                existingSale.setCustomer(
                                findCustomerById(dto.getCustomerId()));

                existingSale.setTable(
                                findStandById(dto.getTableId()));

                SaleEntity updatedSale = saleRepository.save(existingSale);

                return SaleMapper.toDTO(updatedSale);
        }

        // Pay sale
        @Transactional
        public SaleDTO pay(Long id) {

                SaleEntity sale = findSaleById(id);

                validateSaleIsOpen(sale);

                if (sale.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalStateException(
                                        "Cannot pay a sale with no products");
                }

                sale.setStatus(SaleStatus.PAID);

                SaleEntity paidSale = saleRepository.save(sale);

                return SaleMapper.toDTO(paidSale);
        }

        // Cancel sale
        @Transactional
        public SaleDTO cancel(Long id) {

                SaleEntity sale = findSaleById(id);

                validateSaleIsOpen(sale);

                sale.setStatus(SaleStatus.CANCELLED);

                SaleEntity cancelledSale = saleRepository.save(sale);

                return SaleMapper.toDTO(cancelledSale);
        }

        private void validateSale(SaleDTO dto) {

                if (dto.getCustomerId() == null) {
                        throw new IllegalArgumentException(
                                        "Customer ID cannot be null");
                }

                if (dto.getTableId() == null) {
                        throw new IllegalArgumentException(
                                        "Table ID cannot be null");
                }
        }

        public void validateSaleIsOpen(SaleEntity sale) {

                if (sale.getStatus() != SaleStatus.OPEN) {
                        throw new IllegalStateException(
                                        "Sale cannot be modified because its status is "
                                                        + sale.getStatus());
                }
        }

        private SaleEntity findSaleById(Long id) {

                return saleRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Sale not found with id: " + id));
        }

        private CustomerEntity findCustomerById(Long customerId) {

                return customerRepository.findById(customerId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found with id: "
                                                                + customerId));
        }

        private StandEntity findStandById(Long standId) {

                return standRepository.findById(standId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stand not found with id: "
                                                                + standId));
        }

}