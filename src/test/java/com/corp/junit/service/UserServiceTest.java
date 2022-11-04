package com.corp.junit.service;

import com.corp.junit.TestBase;
import com.corp.junit.dao.UserDao;
import com.corp.junit.dto.UserDto;
import com.corp.junit.extension.ConditionalExtension;
import com.corp.junit.extension.PostProcessionExtension;
import com.corp.junit.extension.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({UserServiceParamResolver.class,
        //GlobalExtension.class
        PostProcessionExtension.class,
        ConditionalExtension.class,
//        ThrowableExtension.class,
        MockitoExtension.class,
})
@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest extends TestBase {
    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;
    private static final UserDto IVAN = UserDto.of(1, "Ivan", "123");
    private static final UserDto PETR = UserDto.of(2, "Petr", "111");


//    DI example
//    UserServiceTest(TestInfo testInfo) {
//
//    }

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
//        this.userDao = Mockito.mock(UserDao.class);
        this.userService = new UserService(userDao);
    }


    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
        Mockito.doReturn(true)
               .when(userDao)
               .delete(Mockito.any());
        boolean deleteResult = userService.delete(IVAN.getId());

//        Такой способ тоже работает, но лучше его не использовать (Первый предпочтительнее), т.к. он имеет ряд ограничений. Но он удобен, если мы хотим последовательно возвращать значения, в случае если мы несколько раз будем вызывать метод delete
//        Mockito.when(userDao.delete(Mockito.any()))
//               .thenReturn(true) // При первом вызове вернет true
//               .thenReturn(false); // при втором - false

//        var argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.times(1)).delete(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());
        assertThat(deleteResult).isTrue();
    }


    @Test
//    @Disabled
//    @RepeatedTest(3)
    void checkLoginFunctionalityPerformance() {
        assertTimeout(Duration.ofMillis(299L), () -> userService.login("dummy", "111"));

        System.out.println(Thread.currentThread()
                                 .getName());
        assertTimeoutPreemptively(Duration.ofMillis(299L), () -> {
//                    Thread.sleep(300L);
                    System.out.println(Thread.currentThread()
                                             .getName());
                    userService.login("dummy", "111");
                }

        );
    }

    @Test
    @Order(3)
    void usersEmptyIfNotUserAdded() {
        System.out.println("Test 1: " + this);
        var userDtoList = userService.getAll();
        assertTrue(userDtoList.isEmpty(), "User list should be empty");
    }

    @Test
    @Order(1)
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN, PETR);
        List<UserDto> userDtoList = userService.getAll();
//        assertEquals(2, userDtoList.size());
        assertThat(userDtoList).hasSize(2);
        MatcherAssert.assertThat(userDtoList, IsCollectionWithSize.hasSize(2));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, UserDto> userDtoMap = userService.getAllConvertedById();
        assertAll(() -> assertThat(userDtoMap).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(userDtoMap).containsValues(IVAN, PETR));

        MatcherAssert.assertThat(userDtoMap, IsMapContaining.hasKey(IVAN.getId()));
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }

    @DisplayName("test user login functionality")
    @Tag("login")
    @Nested
    class LoginTest {

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

        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<UserDto> maybeUserDto = userService.login(IVAN.getUsername(), IVAN.getPassword());
//        assertTrue(maybeUserDto.isPresent());
//        maybeUserDto.ifPresent(userDto -> assertEquals(IVAN, userDto));
            assertThat(maybeUserDto).isPresent();
            maybeUserDto.ifPresent(userDto -> assertThat(userDto).isEqualTo(maybeUserDto.get()));
        }

        @Test
        @Order(2)
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

        @ParameterizedTest(name = "{arguments} test")
//    @ArgumentsSource()
//        @NullSource
//        @EmptySource
//    @NullAndEmptySource
//        @ValueSource(strings = {"Ivan", "Petr"})
//    @EnumSource
        @MethodSource("com.corp.junit.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//        @CsvSource({"Ivan,123", "Petr,111"})
        void loginParameterizedTest(String username, String password, Optional<UserDto> userDto) {
            userService.add(IVAN, PETR);

            var mayBeUserDto = userService.login(username, password);
            assertThat(mayBeUserDto).isEqualTo(userDto);
        }

    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty()
                )
        );
    }
}