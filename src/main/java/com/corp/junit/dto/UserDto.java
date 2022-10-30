package com.corp.junit.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class UserDto {

    Integer id;
    String username;
    String password;
}
