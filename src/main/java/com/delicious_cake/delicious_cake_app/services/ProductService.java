package com.delicious_cake.delicious_cake_app.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.delicious_cake.delicious_cake_app.dtos.ProductDTO;
import com.delicious_cake.delicious_cake_app.entities.CategoryEntity;
import com.delicious_cake.delicious_cake_app.entities.ProductEntity;
import com.delicious_cake.delicious_cake_app.mappers.ProductMapper;
import com.delicious_cake.delicious_cake_app.repositories.CategoryRepository;
import com.delicious_cake.delicious_cake_app.repositories.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStorageService imageStorageService;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ImageStorageService imageStorageService) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageStorageService = imageStorageService;
    }

    // Create Method
    public ProductDTO create(ProductDTO dto) {

        validateProduct(dto);

        ProductEntity product = ProductMapper.toEntity(dto);

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with id: " + dto.getCategoryId()));

        product.setCategory(category);

        ProductEntity savedProduct = productRepository.save(product);

        return ProductMapper.toDTO(savedProduct);
    }

    // Get Method
    public ProductDTO getById(Long id) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        return ProductMapper.toDTO(product);
    }

    // Get All Method
    public List<ProductDTO> getAll() {

        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Update Method
    public ProductDTO update(Long id, ProductDTO dto) {

        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        validateProduct(dto);

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with id: " + dto.getCategoryId()));

        existingProduct.setName(dto.getName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setCategory(category);

        ProductEntity updatedProduct = productRepository.save(existingProduct);

        return ProductMapper.toDTO(updatedProduct);
    }

    // Delete Method
    public void delete(Long id) {

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    //Update an Image
    public ProductDTO updateImage(Long id, MultipartFile image) throws IOException {

        ProductEntity product = findProductById(id);

        String previousImage = product.getImageUrl();

        String newImage = imageStorageService.store(image);

        product.setImageUrl(newImage);

        try {
            ProductEntity savedProduct = productRepository.save(product);

            if (previousImage != null) {
                imageStorageService.delete(previousImage);
            }

            return ProductMapper.toDTO(savedProduct);

        } catch (RuntimeException | IOException exception) {

            imageStorageService.delete(newImage);

            throw exception;
        }
    }

    // Find Entity
    private ProductEntity findProductById(Long id) {
    return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Product not found with id: " + id));
}

    // Validation Method
    private void validateProduct(ProductDTO dto) {

        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name cannot be null or empty");
        }

        if (dto.getPrice() == null ||
                dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Product price cannot be null or negative");
        }

        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException(
                    "Product category ID cannot be null");
        }
    }
}