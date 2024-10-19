package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

//    @GetMapping(path = "/users")
//    public List<User> getUsers(){
//        return userService.getAll();
//    }

    @GetMapping()
    public String get(){
        return "GET:: admin controller";
    }

    @PostMapping()
    public String post(){
        return "POST:: admin controller";
    }

    @PutMapping()
    public String put(){
        return "PUT:: admin controller";
    }

    @DeleteMapping()
    public String delete(){
        return "DELETE:: admin controller";
    }
}
