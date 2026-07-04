package com.example.server.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {

    Long userId;
    String name;
    String surname;
    String email;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
