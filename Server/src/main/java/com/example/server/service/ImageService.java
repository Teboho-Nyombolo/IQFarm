package com.example.server.service;

import com.example.server.dto.response.ImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    ImageResponseDTO uploadImage(MultipartFile file, String description, Long cropId, Long trackerId, Long userId);

    ImageResponseDTO getImageById(Long imageId);

    List<ImageResponseDTO> getImagesByCrop(Long cropId);

    List<ImageResponseDTO> getImagesByTracker(Long trackerId);

    List<ImageResponseDTO> getImagesByUser(Long userId);

    List<ImageResponseDTO> getAllImages();

    void deleteImage(Long imageId);

    byte[] getImageBytes(String storedName);
}
