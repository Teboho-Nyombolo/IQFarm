package com.example.server.dto.request;

import lombok.Data;

@Data
public class UserRequestDTO {

    String name;
    String surname;
    String email;
    String password;
}
