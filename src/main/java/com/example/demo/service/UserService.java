package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    @Autowired
    public UserService(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtService;

    public List<User> getAll(){
        return userRepo.findAll();
    }

    /**
     * We are manually getting the info of the user
     * and verify it
     * @param user this is the user that we are verifying
     * @return Provide the JWT else "Fail"
     */
    public String verify(User user) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),user.getPassword()));

        if (authentication.isAuthenticated())
            return jwtService.generateToken(user.getUsername()) +
                    "\nlogged as " + user.getUsername();

        return "Fail";
    }

    public void register(User user){
        user.setPassword(encoder.encode(user.getPassword()));

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Collection<Role> roles = new ArrayList<>();
            for (Role role : user.getRoles()){
                // Fetch role from the database using roleRepo
                Role fetchedRole = roleRepo.findByName(role.getName());
                if (fetchedRole != null){
                    roles.add(fetchedRole); // Add the valid role to the collection
                } else
                    // Handle the case where a role doesn't exist
                    System.out.println("Role not found: " + role.getName());
            }
            user.setRoles(roles); // Set the role during the registration
        }
        // Save the user to the database
        userRepo.save(user);
    }
}
