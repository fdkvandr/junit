package com.corp.junit.service;

import com.corp.junit.dto.UserDto;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private UserService userService;
    private static final UserDto IVAN = UserDto.of(1, "Ivan", "123");
    private static final UserDto PETR = UserDto.of(2, "Petr", "111");


    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNotUserAdded() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "User list should be empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        List<UserDto> userDtoList = userService.getAll();
        assertEquals(2, userDtoList.size());
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(IVAN);
        Optional<UserDto> maybeUserDto = userService.login(IVAN.getUsername(), IVAN.getPassword());
        assertTrue(maybeUserDto.isPresent());
        maybeUserDto.ifPresent(userDto -> assertEquals(IVAN, userDto));
    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<UserDto> maybeUserDto = userService.login(IVAN.getUsername(), "dummy");
        assertTrue(maybeUserDto.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);
        Optional<UserDto> maybeUserDto = userService.login("dummy", "dummy");
        assertTrue(maybeUserDto.isEmpty());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }
}
