package com.example.demo.username;

import com.example.demo.exception.DuplicateUsernameException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserClassTest {

    private final UserService userService;
    private final UserRepository userRepository;

//    @Autowired
//    private BCryptPasswordEncoder encoder;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    UserClassTest(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private final String VALID_USERNAME_EXAMPLE = "UserName";
    private final String VALID_PASSWORD_INPUT_EXAMPLE = "password.123";


    @BeforeEach
    void setUp(){
        System.out.println("Print before each test");
    }

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("Should Create User")
    public void shouldCreateContact() {
        String encryptedPassword = encoder.encode(VALID_PASSWORD_INPUT_EXAMPLE);
        User user = new User(
                VALID_USERNAME_EXAMPLE,
                encoder.encode(VALID_PASSWORD_INPUT_EXAMPLE)
        );
        assertDoesNotThrow(() -> userService.register(user));

        assertFalse(userRepository.findAll().isEmpty()); // Verify the list is not empty
        assertEquals(1, userRepository.findAll().size()); // Verify there is just one user

        // Check that the user has the correct username and the password matches the original
        assertTrue(userService.getAll().stream()
                .anyMatch(user1 -> user1.getUsername().equals(VALID_USERNAME_EXAMPLE) &&
                        user1.getPassword().equals(encryptedPassword)));
    }

    @Nested
    public class UsernameTests{

        @Test
        @DisplayName("Should Not Create When Name Is Null")
        public void shouldThrowIllegalArgumentExceptionWhenUsernameIsNull () {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.register(new User(
                            null,
                            VALID_PASSWORD_INPUT_EXAMPLE
                    )));
        }

        @Test
        @DisplayName("Should  Create Users")
        public void shouldThrowIllegalArgumentExceptionWhenUsernameIsBlank () {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.register(new User(
                            null,
                            VALID_PASSWORD_INPUT_EXAMPLE
                    )));
        }

        @Test
        @DisplayName("Should Not Create When Name Is Invalid")
        public void shouldThrowIllegalArgumentExceptionWhenUsernameIsInvalid() {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.register(new User(
                            "",
                            VALID_PASSWORD_INPUT_EXAMPLE
                    )));
        }
    }



}
