package com.example.server.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {

    Long id;
    String name;
    String surname;
    String email;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
