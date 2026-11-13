package com.delicious_cake.delicious_cake_app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    private final Path uploadDirectory =
            Paths.get("uploads/products").toAbsolutePath().normalize();

    public String store(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be empty");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Image cannot exceed 10 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !List.of("image/jpeg", "image/png", "image/webp")
                        .contains(contentType)) {
            throw new IllegalArgumentException("Unsupported image format");
        }

        Files.createDirectories(uploadDirectory);

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Unsupported format");
        };

        String filename = UUID.randomUUID() + extension;

        Path destination = uploadDirectory.resolve(filename).normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.copy(file.getInputStream(), destination);

        return "/uploads/products/" + filename;
    }

    public void delete(String imageUrl) throws IOException {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String filename = Paths.get(imageUrl).getFileName().toString();

        Path file = uploadDirectory.resolve(filename).normalize();

        if (file.startsWith(uploadDirectory)) {
            Files.deleteIfExists(file);
        }
    }
}