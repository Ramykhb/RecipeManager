package com.project.recipemanager.User.Controller;

import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import com.project.recipemanager.User.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers()
    {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/signup")
    public ResponseEntity addUser(@RequestBody User user, HttpSession session)
    {
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody User user, HttpSession session)
    {
        String tempId = userService.login(user.getUsername(), user.getPassword());
        if (tempId != "")
            session.setAttribute("sessionId", tempId);
        return ResponseEntity.ok().build();
    }
}
