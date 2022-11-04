package com.corp.junit.service;

import com.corp.junit.dao.UserDao;
import com.corp.junit.dto.UserDto;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserService {

    private final List<UserDto> userDtoList = new ArrayList<>();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean delete(Integer userId)  {
        return userDao.delete(userId);
    }

    public List<UserDto> getAll() {
        return userDtoList;
    }

    public boolean add(UserDto... userDtos) {
        return userDtoList.addAll(Arrays.asList(userDtos));
    }

    public Optional<UserDto> login(String username, String password) {
        if (username == null || password == null){
            throw new IllegalArgumentException("username or password is null");
        }

        return userDtoList.stream()
                          .filter(userDto -> userDto.getUsername()
                                                    .equals(username))
                          .filter(userDto -> userDto.getPassword()
                                                    .equals(password))
                          .findFirst();
    }

    public Map<Integer, UserDto> getAllConvertedById() {
        return userDtoList.stream()
                          .collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }
}
