package com.corp.junit.service;

import com.corp.junit.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final List<UserDto> userDtoList = new ArrayList<>();

    public List<UserDto> getAll() {
        return userDtoList;
    }

    public boolean add(UserDto userDto) {
        return userDtoList.add(userDto);
    }

    public Optional<UserDto> login(String username, String password) {
        return userDtoList.stream()
                          .filter(userDto -> userDto.getUsername()
                                                    .equals(username))
                          .filter(userDto -> userDto.getPassword()
                                                    .equals(password))
                          .findFirst();
    }
}
