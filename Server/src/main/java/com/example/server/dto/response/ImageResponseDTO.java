package com.example.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponseDTO {

    private Long imageId;
    private String originalName;
    private String storedName;
    private String fileUrl;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String description;
    private Long uploadedById;
    private String uploadedByName;
    private Long cropId;
    private Long trackerId;

}
