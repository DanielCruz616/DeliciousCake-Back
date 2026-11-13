package com.delicious_cake.delicious_cake_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delicious_cake.delicious_cake_app.dtos.SaleDTO;
import com.delicious_cake.delicious_cake_app.dtos.SaleDetailDTO;
import com.delicious_cake.delicious_cake_app.dtos.SaleRequestDTO;
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
        private final SaleDetailService saleDetailService;

        public SaleService(
                        SaleRepository saleRepository,
                        CustomerRepository customerRepository,
                        StandRepository standRepository,
                        SaleDetailService saleDetailService) {

                this.saleRepository = saleRepository;
                this.customerRepository = customerRepository;
                this.standRepository = standRepository;
                this.saleDetailService = saleDetailService;
        }

        // Create sale
        @Transactional
        public SaleDTO create(SaleRequestDTO dto) {

                SaleEntity sale = new SaleEntity();

                sale.setCustomer(
                                dto.getCustomerId() == null
                                                ? null
                                                : findCustomerById(dto.getCustomerId()));

                sale.setTable(
                                dto.getTableId() == null
                                                ? null
                                                : findStandById(dto.getTableId()));

                sale.setCreatedAt(LocalDate.now(ZoneId.of("America/Bogota")));
                sale.setTotal(BigDecimal.ZERO);
                sale.setStatus(SaleStatus.OPEN);

                SaleEntity savedSale = saleRepository.save(sale);

                return SaleMapper.toDTO(savedSale);
        }

        // Get by ID
        @Transactional(readOnly = true)
        public SaleDTO getById(Long id) {

                SaleEntity sale = findSaleById(id);

                return SaleMapper.toDTO(sale);
        }

        // Get all
        @Transactional(readOnly = true)
        public List<SaleDTO> getAll() {

                return saleRepository.findAll()
                                .stream()
                                .map(SaleMapper::toDTO)
                                .toList();
        }

        // Get all open sales
        @Transactional(readOnly = true)
        public List<SaleDTO> getOpenSales() {

                return saleRepository.findByStatus(SaleStatus.OPEN)
                                .stream()
                                .map(SaleMapper::toDTO)
                                .toList();
        }

        // Update sale and its details
        @Transactional
        public SaleDTO update(Long id, SaleRequestDTO dto) {

                SaleEntity existingSale = findSaleById(id);

                validateSaleIsOpen(existingSale);

                existingSale.setCustomer(
                                dto.getCustomerId() == null
                                                ? null
                                                : findCustomerById(dto.getCustomerId()));

                if (dto.getTableId() == null) {
                        throw new IllegalArgumentException(
                                        "Table ID cannot be null");
                }

                existingSale.setTable(
                                findStandById(dto.getTableId()));

                List<SaleDetailDTO> details = saleDetailService.replaceDetails(
                                existingSale,
                                dto.getDetails());

                SaleEntity updatedSale = saleRepository.save(existingSale);

                return SaleMapper.toDTO(updatedSale, details);
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

                return SaleMapper.toDTO(saleRepository.save(sale));
        }

        // Cancel sale
        @Transactional
        public SaleDTO cancel(Long id) {

                SaleEntity sale = findSaleById(id);

                validateSaleIsOpen(sale);

                sale.setStatus(SaleStatus.CANCELLED);

                return SaleMapper.toDTO(saleRepository.save(sale));
        }

        // Validation
        private void validateSaleIsOpen(SaleEntity sale) {

                if (sale.getStatus() != SaleStatus.OPEN) {
                        throw new IllegalStateException(
                                        "Sale cannot be modified because its status is "
                                                        + sale.getStatus());
                }
        }

        // Find methods
        private SaleEntity findSaleById(Long id) {

                return saleRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Sale not found with id: " + id));
        }

        private CustomerEntity findCustomerById(Long customerId) {

                return customerRepository.findById(customerId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Customer not found with id: " + customerId));
        }

        private StandEntity findStandById(Long standId) {

                return standRepository.findById(standId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stand not found with id: " + standId));
        }
}