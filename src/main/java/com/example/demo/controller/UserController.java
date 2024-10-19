package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * NOTES
     *
     * @apiNote
     * Need to take care about user persmission and what can they do
     * Verify all the function
     * Integer to String conversion????
     * Verify STACKOVERFLOW for answers
     */


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/users")
    public List<User> getUsers(){
        return userService.getAll();
    }

    /**
     * Login form
     * @param user the user we are trying to log in with
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestBody User user){
        System.out.println(user);
        return userService.verify(user);
    }


    @PostMapping("/register")
    public void register(@RequestBody User user){
        userService.register(user);
    }

}
