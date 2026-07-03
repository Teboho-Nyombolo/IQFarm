package com.example.server.service;

import com.example.server.dto.request.LoginRequestDTO;
import com.example.server.dto.request.UserRequestDTO;
import com.example.server.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO loginUser(LoginRequestDTO request);
    UserResponseDTO registerUser(UserRequestDTO request);
    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, UserRequestDTO request);
    void deleteUser(Long id);
}
