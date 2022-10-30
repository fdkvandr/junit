package com.corp.junit.service;

import com.corp.junit.dto.UserDto;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        var userDtoList = userService.getAll();
        assertTrue(userDtoList.isEmpty(), () -> "User list should be empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN, PETR);
        List<UserDto> userDtoList = userService.getAll();
//        assertEquals(2, userDtoList.size());
        assertThat(userDtoList).hasSize(2);
        MatcherAssert.assertThat(userDtoList, IsCollectionWithSize.hasSize(2));
    }

    @Test
    @Tag("login")
    void throwExceptionIfUsernameOrPasswordIsNull() {
        assertAll(
                () -> {
                    var e = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
                    assertThat(e.getMessage()).isEqualTo("username or password is null");
                },
                () -> {
                    var e = assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null));
                    assertThat(e.getMessage()).isEqualTo("username or password is null");
                }
        );
    }

    @Test
    @Tag("login")
    void loginSuccessIfUserExists() {
        userService.add(IVAN);
        Optional<UserDto> maybeUserDto = userService.login(IVAN.getUsername(), IVAN.getPassword());
//        assertTrue(maybeUserDto.isPresent());
//        maybeUserDto.ifPresent(userDto -> assertEquals(IVAN, userDto));
        assertThat(maybeUserDto).isPresent();
        maybeUserDto.ifPresent(userDto -> assertThat(userDto).isEqualTo(maybeUserDto.get()));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, UserDto> userDtoMap = userService.getAllConvertedById();
        assertAll(() -> assertThat(userDtoMap).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(userDtoMap).containsValues(IVAN, PETR));

        MatcherAssert.assertThat(userDtoMap, IsMapContaining.hasKey(IVAN.getId()));
    }

    @Test
    @Tag("login")
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<UserDto> maybeUserDto = userService.login(IVAN.getUsername(), "dummy");
        assertTrue(maybeUserDto.isEmpty());
    }

    @Test
    @Tag("login")
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
