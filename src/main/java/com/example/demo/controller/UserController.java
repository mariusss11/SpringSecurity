package com.example.demo.controller;

import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
//@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * NOTES
     * Need to take care about user's permissions and what can they do
     * Verify all the function
     * Verify STACKOVERFLOW for answers
     */


    @GetMapping(path = "/users")
    public List<User> getUsers(){
        return userService.getAll();
    }

//    USING DTO
    @GetMapping(path = "/usersDTO")
    public List<UserDTO> getUsersDTO(){
        List<User> users = userService.getAll();
        return users.stream()
            .map(user -> new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getRoles().stream()
                            .map(role -> new RoleDTO(role.getId(), role.getName()))
                            .toList()
            ))
                .toList();
    }

    /**
     * Login form
     * @param user the user we are trying to log in with
     * @return JWT
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
