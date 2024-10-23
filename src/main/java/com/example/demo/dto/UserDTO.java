package com.example.demo.dto;

import com.example.demo.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO - Data Transfer Object <br>
 * A DTO, is a design pattern used to transfer data
 * between software application subsystems or layers.<br>
 * The primary purpose of a DTO is to encapsulate data
 * and send it from one part of an application to another,
 * especially over a network.
 */
public class UserDTO {

    private int id;
    /*
     * I could use @JsonProperty
     * for manipulating the name of the
     * variable when I output it

    @JsonProperty("user_name")
    */
    private String username;
    private String password;
    private List<RoleDTO> roles;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(int id, String username, List<RoleDTO> roles) {
        this.id = id;
        this.username = username;
//        this.password = password;
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
