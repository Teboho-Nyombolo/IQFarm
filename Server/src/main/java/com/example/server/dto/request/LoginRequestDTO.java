package com.example.server.dto.request;

import lombok.Data;

@Data
public class LoginRequestDTO {

    String email;
    String password;
}
