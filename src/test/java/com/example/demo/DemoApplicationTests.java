package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class DemoApplicationTests {

	private final UserService userService;
	private final UserRepository userRepository;

    DemoApplicationTests(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

	private final String VALID_USERNAME_EXAMPLE = "UserName";
	private final String VALID_PASSWORD_EXAMPLE = "UserName";
	private final String VALID_EMAIL_EXAMPLE = "student.name123@example.org";



	@BeforeEach
	void setUp(){
		System.out.println("Print before each test");
	}

	@AfterEach
	void tearDown(){
		userRepository.deleteAll();
	}
}
