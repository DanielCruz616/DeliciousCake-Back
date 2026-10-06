package com.delicious_cake.delicious_cake_app.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.delicious_cake.delicious_cake_app.dtos.SaleDTO;
import com.delicious_cake.delicious_cake_app.services.SaleService;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleDTO> create(@RequestBody SaleDTO saleDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saleService.create(saleDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAll() {
        return ResponseEntity.ok(saleService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleDTO> update(
            @PathVariable Long id,
            @RequestBody SaleDTO saleDTO) {

        return ResponseEntity.ok(saleService.update(id, saleDTO));
    }
}