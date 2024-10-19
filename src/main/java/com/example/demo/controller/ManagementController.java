package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
public class ManagementController {

//    @GetMapping(path = "/users")
//    public List<User> getUsers(){
//        return userService.getAll();
//    }

    @GetMapping()
    public String get(){
        return "GET:: management controller";
    }

    @PostMapping()
    public String post(){
        return "POST:: management controller";
    }

    @PutMapping()
    public String put(){
        return "PUT:: management controller";
    }

    @DeleteMapping()
    public String delete(){
        return "DELETE:: management controller";
    }
}
