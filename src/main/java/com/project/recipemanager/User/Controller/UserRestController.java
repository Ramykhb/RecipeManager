package com.project.recipemanager.User.Controller;

import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getUsers()
    {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping
    public ResponseEntity addUser(@RequestBody User user)
    {
        userRepository.save(user);
        System.out.println("HELLLO");
        return ResponseEntity.ok().build();
    }
}
