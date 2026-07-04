package com.example.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUploadRequestDTO {

    private MultipartFile file;
    private String description;
    private Long cropId;
    private Long trackerId;
}
