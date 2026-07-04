package com.example.server.service.impl;

import com.example.server.dto.response.ImageResponseDTO;
import com.example.server.entity.Crop;
import com.example.server.entity.CropTracker;
import com.example.server.entity.Image;
import com.example.server.entity.User;
import com.example.server.mapper.ImageMapper;
import com.example.server.repository.CropRepository;
import com.example.server.repository.CropTrackerRepository;
import com.example.server.repository.ImageRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final CropTrackerRepository cropTrackerRepository;
    private final ImageMapper imageMapper;

    @Value("${app.file.storage-path:uploads/images/}")
    private String storagePath;

    @Value("${app.file.max-size:10485760}")
    private Long maxFileSize;

    public ImageServiceImpl(ImageRepository imageRepository, UserRepository userRepository, CropRepository cropRepository, CropTrackerRepository cropTrackerRepository, ImageMapper imageMapper) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.cropRepository = cropRepository;
        this.cropTrackerRepository = cropTrackerRepository;
        this.imageMapper = imageMapper;
    }

    @Override
    public ImageResponseDTO uploadImage(MultipartFile file, String description, Long cropId, Long trackerId, Long userId) {
        validateFile(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        String storedName = UUID.randomUUID().toString() + extension;

        Path targetPath = Paths.get(storagePath).resolve(storedName).toAbsolutePath().normalize();

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalName, e);
        }

        Image image = Image.builder()
                .originalName(originalName)
                .storedName(storedName)
                .filePath(targetPath.toString())
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .description(description)
                .uploadedBy(user)
                .build();

        if (cropId != null) {
            Crop crop = cropRepository.findById(cropId)
                    .orElseThrow(() -> new EntityNotFoundException("Crop not found with id: " + cropId));
            image.setCrop(crop);
        }

        if (trackerId != null) {
            CropTracker tracker = cropTrackerRepository.findById(trackerId)
                    .orElseThrow(() -> new EntityNotFoundException("CropTracker not found with id: " + trackerId));
            image.setCropTracker(tracker);
        }

        Image savedImage = imageRepository.save(image);
        return imageMapper.toResponse(savedImage);
    }

    @Override
    public ImageResponseDTO getImageById(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + imageId));
        return imageMapper.toResponse(image);
    }

    @Override
    public List<ImageResponseDTO> getImagesByCrop(Long cropId) {
        return imageRepository.findByCrop_CropId(cropId).stream()
                .map(imageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageResponseDTO> getImagesByTracker(Long trackerId) {
        return imageRepository.findByCropTracker_TrackerId(trackerId).stream()
                .map(imageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageResponseDTO> getImagesByUser(Long userId) {
        return imageRepository.findByUploadedBy_UserId(userId).stream()
                .map(imageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageResponseDTO> getAllImages() {
        return imageRepository.findAll().stream()
                .map(imageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + imageId));

        try {
            Path filePath = Paths.get(image.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + image.getStoredName(), e);
        }

        imageRepository.delete(image);
    }

    @Override
    public byte[] getImageBytes(String storedName) {
        try {
            Path filePath = Paths.get(storagePath).resolve(storedName).toAbsolutePath().normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + storedName, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
