package com.project.recipemanager.User.Controller;

import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import com.project.recipemanager.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody User user, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User already logged in");
        String newUserId = userService.signup(user.getUsername(), user.getPassword());
        if (!newUserId.equals("")) {
            HttpSession sessionNew = request.getSession(true);
            sessionNew.setAttribute("sessionId", newUserId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User already logged in");
        String tempId = userService.login(user.getUsername(), user.getPassword());
        if (!tempId.equals("")) {
            HttpSession sessionNew = request.getSession(true);
            sessionNew.setAttribute("sessionId", tempId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            session.invalidate();
        System.out.println("HELLO");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Logged out");
    }

}
