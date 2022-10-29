package com.corp.junit.service;

import com.corp.junit.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final List<UserDto> userDtoList = new ArrayList<>();

    public List<UserDto> getAll() {
        return userDtoList;
    }

    public boolean add(UserDto userDto) {
        return userDtoList.add(userDto);
    }
}
