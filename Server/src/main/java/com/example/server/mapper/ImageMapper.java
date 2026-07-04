package com.example.server.mapper;

import com.example.server.dto.response.ImageResponseDTO;
import com.example.server.entity.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    @Value("${app.file.storage-url:http://localhost:8080/uploads/images/}")
    private String baseUrl;

    public ImageResponseDTO toResponse(Image image) {
        return ImageResponseDTO.builder()
                .imageId(image.getImageId())
                .originalName(image.getOriginalName())
                .storedName(image.getStoredName())
                .fileUrl(baseUrl + image.getStoredName())
                .mimeType(image.getMimeType())
                .fileSize(image.getFileSize())
                .uploadedAt(image.getUploadedAt())
                .description(image.getDescription())
                .uploadedById(image.getUploadedBy() != null ? image.getUploadedBy().getUserId() : null)
                .uploadedByName(image.getUploadedBy() != null ?
                        image.getUploadedBy().getName() + " " + image.getUploadedBy().getSurname() : null)
                .cropId(image.getCrop() != null ? image.getCrop().getCropId() : null)
                .trackerId(image.getCropTracker() != null ? image.getCropTracker().getTrackerId() : null)
                .build();
    }
}