package com.example.demo.username;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.DuplicateUsernameException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.xml.namespace.QName;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@SpringBootTest
@AutoConfigureMockMvc
class UserClassTest {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    UserClassTest(UserService userService,
                  UserRepository userRepository,
                  ObjectMapper objectMapper,
                  MockMvc mockMvc
    ) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    private final String VALID_USERNAME_EXAMPLE = "UserName";
    private final String VALID_PASSWORD_INPUT_EXAMPLE = "password@123";


//    @BeforeEach
//    void setUp() throws DuplicateUsernameException {
//        System.out.println("Print before each test");
//        User user = new User(
//                "marius",
//                "marius@"
//        );
//        userService.register(user);
//    }

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("Should Create User")
    public void shouldCreateContact() {
        User user = new User(
                VALID_USERNAME_EXAMPLE,
                VALID_PASSWORD_INPUT_EXAMPLE
        );
        assertDoesNotThrow(() -> userService.register(user));

        assertFalse(userRepository.findAll().isEmpty()); // Verify the list is not empty
        assertEquals(1, userRepository.findAll().size()); // Verify there is just one user

        // Check that the user has the correct username and the password matches the original
        assertTrue(userService.getAll().stream()
                .anyMatch(user1 -> user.getUsername().equals(VALID_USERNAME_EXAMPLE) &&
                        encoder.matches(VALID_PASSWORD_INPUT_EXAMPLE,user.getPassword())));
    }

    @Test
    public void shouldCreateMoreUsers(){
        User user = new User(
                VALID_USERNAME_EXAMPLE,
                VALID_PASSWORD_INPUT_EXAMPLE
        );
        assertDoesNotThrow(() -> userService.register(user));

        User user1 = new User(
                "differentUsername",
                "strong@password"
        );
        assertDoesNotThrow(() -> userService.register(user1));

        assertFalse(userRepository.findAll().isEmpty()); // Verify the list is not empty
        assertEquals(2, userRepository.findAll().size()); // Verify there is just one user

        // Check that the user has the correct username and the password matches the original
        assertTrue(userService.getAll().stream()
                .anyMatch(userType ->
                                userType.getUsername().equals(VALID_USERNAME_EXAMPLE) &&
                                        encoder.matches(VALID_PASSWORD_INPUT_EXAMPLE, userType.getPassword())));
        // Verify that the second user has the correct username and password matches
        assertTrue(userService.getAll().stream()
                .anyMatch(userType ->
                                userType.getUsername().equals("differentUsername") &&
                                        encoder.matches("strong@password", userType.getPassword())));
    }

    @DisplayName("Repeat User Creation for 5 times")
    @RepeatedTest(value = 5,
            name = "Repeating User Creation Test {currentRepetition} of {totalRepetition}")
    public void shouldCreateUserRepeatedly(){
        User user = new User(
                VALID_USERNAME_EXAMPLE,
                VALID_PASSWORD_INPUT_EXAMPLE
        );
        assertDoesNotThrow(() -> userService.register(user));
        assertFalse(userRepository.findAll().isEmpty()); // Verify the list is not empty
        assertEquals(1, userRepository.findAll().size()); // Verify there is just one user
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
        @DisplayName("Should Create Users")
        public void shouldThrowIllegalArgumentExceptionWhenUsernameIsBlank () {
            assertThrows(IllegalArgumentException.class, () ->
                    userService.register(new User(
                            "",
                            VALID_PASSWORD_INPUT_EXAMPLE
                    )));
        }
    }

    @Nested
    class PasswordTests{

        @Test
        public void shouldThrowIllegalStateExceptionWhenPasswordIsNull(){
            assertThrows(IllegalArgumentException.class, () ->
                    userService.register(new User(
                            VALID_USERNAME_EXAMPLE,
                            null
                    )));
        }

        @Test
        public void shouldThrowIllegalStateExceptionWhenPasswordIsBlank(){
            assertThrows(IllegalArgumentException.class, () ->
                    userService.register(new User(
                            VALID_USERNAME_EXAMPLE,
                            ""
                    )));
        }

        @Test
        public void shouldThrowIllegalStateExceptionWhenPasswordIsWeak(){
            assertThrows(IllegalArgumentException.class, () ->
                    userService.register(new User(
                            VALID_USERNAME_EXAMPLE,
                            "weakPassword"
                    )));
        }

        @DisplayName("Test Different Passwords")
        @ParameterizedTest
        @ValueSource(strings = {"password@strong", "strong@password","notWeak@password"})
        public void shouldTestStudentCreationWithValueSources(String password){
            assertDoesNotThrow(() -> userService.register(new User(VALID_USERNAME_EXAMPLE, password)));
            assertFalse(userRepository.findAll().isEmpty());
            assertEquals(1,userRepository.findAll().size());
        }
    }

    @Test
    @DisplayName("Should Encrypt Password")
    public void shouldEncryptThePassword(){
        User user = new User(
                VALID_USERNAME_EXAMPLE,
                VALID_PASSWORD_INPUT_EXAMPLE
        );
        assertDoesNotThrow(() -> userService.register(user));
        String databasePassword = user.getPassword();
        assertTrue(databasePassword.startsWith("$2a$12$"));
    }

    @Test
    @DisplayName("Should Login Successful")
    public void shouldLoginSuccessful() throws Exception {
        User user = new User(
                VALID_USERNAME_EXAMPLE,
                VALID_PASSWORD_INPUT_EXAMPLE
        );
        assertDoesNotThrow(() -> userService.register(user));

        assertFalse(userRepository.findAll().isEmpty()); // Verify the list is not empty
        assertEquals(1, userRepository.findAll().size()); // Verify there is just one user


        UserDTO userDTO = new UserDTO(
                VALID_USERNAME_EXAMPLE,
                VALID_PASSWORD_INPUT_EXAMPLE
        );


        String userJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)) // Attach the JSON payload
                .andExpect(status().isOk()) // Check for 200 OK status
                ;
    }









}
