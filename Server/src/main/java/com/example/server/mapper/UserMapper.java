
package com.example.server.mapper;

import com.example.server.dto.request.UserRequestDTO;
import com.example.server.dto.response.UserResponseDTO;
import com.example.server.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO request) {
        if (request == null) return null;

        return User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

    }
}