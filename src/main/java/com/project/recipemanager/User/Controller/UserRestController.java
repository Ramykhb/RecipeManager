package com.project.recipemanager.User.Controller;

import com.project.recipemanager.Recipe.Model.Recipe;
import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import com.project.recipemanager.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/loggedInUser")
    @Operation(summary = "Retrieve logged in user", description = "Retrieve details about the logged in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found in database", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Not Found - User not found\"}")
            ))
    })
    public ResponseEntity<?> loggedInUser(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Optional<User> userOptional = userRepository.findById(session.getAttribute("sessionId").toString());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign up user", description = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully signed up user", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User already logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User already logged in\"}")
            )),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Conflict - User already exists\"}")
            ))
    })
    public ResponseEntity<?> signupUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(
                            name = "SampleUser",
                            summary = "Example of user credentials",
                            value = "{\n" +
                                    "  \"username\": \"ahmad\",\n" +
                                    "  \"password\": \"ahmadassaf\"\n" +
                                    "}"
                    )
            )
    ) @RequestBody User user, HttpServletRequest request) {
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
    @Operation(summary = "Log in user", description = "Login user to existing account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in user", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User already logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User already logged in\"}")
            ))
    })
    public ResponseEntity<?> loginUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(
                            name = "SampleUser",
                            summary = "Example of user credentials",
                            value = "{\n" +
                                    "  \"username\": \"ahmad\",\n" +
                                    "  \"password\": \"ahmadassaf\"\n" +
                                    "}"
                    )
            )
    ) @RequestBody User user, HttpServletRequest request) {
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
    @Operation(summary = "Log out user", description = "logout user of account")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Successfully logged out user", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            ))
    })
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            session.invalidate();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Logged out");
    }

}
