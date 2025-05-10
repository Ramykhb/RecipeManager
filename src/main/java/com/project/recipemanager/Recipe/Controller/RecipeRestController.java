package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Model.Recipe;
import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import com.project.recipemanager.Recipe.Service.RecipeService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    private final RecipeService recipeService;

    @Autowired
    public RecipeRestController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    @Operation(summary = "Get all recipes", description = "Retrieve a list of all recipes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of recipes", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity<?> getRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        List<Recipe> recipes = recipeRepository.findAll();
        for (Recipe recipe : recipes)
        {
            Optional<User> userOptional = userRepository.findById(recipe.getAuthor());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
        }

        return ResponseEntity.ok(recipes);
    }

    @PostMapping
    @Operation(summary = "Add a new recipe", description = "Add a new recipe to the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully added a new recipe", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity addRecipe(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Recipe to be created",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Recipe.class),
                    examples = @ExampleObject(
                            name = "SampleRecipe",
                            summary = "Example recipe JSON",
                            value = "{\n" +
                                    "  \"title\": \"Sushi\",\n" +
                                    "  \"description\": \"Simple homemade sushi with salmon and avocado.\",\n" +
                                    "  \"ingredients\": [\"sushi rice\", \"nori\", \"salmon\", \"avocado\"],\n" +
                                    "  \"instructions\": [\"Cook rice\", \"Place on nori\", \"Add fillings\", \"Roll and cut\"],\n" +
                                    "  \"cookingTime\": \"50\",\n" +
                                    "  \"category\": \"Main Dish\"\n" +
                                    "}"
                    )
            )
    ) @RequestBody Recipe recipe, HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(dateFormatter);
        recipe.setCreatedAt(formattedDate);
        recipe.setAuthor(session.getAttribute("sessionId").toString());
        recipe.setImageURL(recipeService.checkURL(recipe.getImageURL(), "https://www.cezarskitchen.com.my/wp-content/themes/consultix/images/no-image-found-360x250.png"));
        recipeRepository.save(recipe);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search for a recipe", description = "Search for a recipe based on title in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipes that match", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity<?> searchByTitle(@RequestParam String title, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        List<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(title);
        for (Recipe recipe : recipes)
        {
            Optional<User> userOptional = userRepository.findById(recipe.getAuthor());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
        }
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/my-posts")
    @Operation(summary = "View my posts", description = "Retrieve recipes posted by logged in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipes that are posted by user", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity<List<Recipe>> getMyRecipes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String userId = session.getAttribute("sessionId").toString();
        return ResponseEntity.ok((List<Recipe>) recipeRepository.findByAuthor(userId));
    }

    @PutMapping("my-posts/{id}")
    @Operation(summary = "Update a single recipe", description = "Update a single recipe based on its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated recipe", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Recipe doesn't belong to logged in user", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Forbidden - Recipe doesn't belong to user\"}")
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Recipe not found in database", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Not Found - Recipe not found\"}")
            ))
    })
    public ResponseEntity<?> editRecipe(@PathVariable String id, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Recipe to update",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Recipe.class),
                    examples = @ExampleObject(
                            name = "SampleRecipe",
                            summary = "Example recipe JSON",
                            value = "{\n" +
                                    "  \"title\": \"Sushi\",\n" +
                                    "  \"description\": \"Simple homemade sushi with salmon and avocado.\",\n" +
                                    "  \"ingredients\": [\"sushi rice\", \"nori\", \"salmon\", \"avocado\"],\n" +
                                    "  \"instructions\": [\"Cook rice\", \"Place on nori\", \"Add fillings\", \"Roll and cut\"],\n" +
                                    "  \"cookingTime\": \"50\",\n" +
                                    "  \"category\": \"Main Dish\"\n" +
                                    "}"
                    )
            )
    ) @RequestBody Recipe updatedRecipe, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String userId = session.getAttribute("sessionId").toString();
        return recipeRepository.findById(id).map(recipe -> {
            if (!recipe.getAuthor().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            recipe.setTitle(updatedRecipe.getTitle());
            recipe.setDescription(updatedRecipe.getDescription());
            recipe.setIngredients(updatedRecipe.getIngredients());
            recipe.setInstructions(updatedRecipe.getInstructions());
            recipe.setCookingTime(updatedRecipe.getCookingTime());
            recipe.setCategory(updatedRecipe.getCategory());
            recipe.setImageURL(recipeService.checkURL(updatedRecipe.getImageURL(), "https://www.cezarskitchen.com.my/wp-content/themes/consultix/images/no-image-found-360x250.png"));
            recipeRepository.save(recipe);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a single recipe", description = "Delete a single recipe based on its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted recipe", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Recipe doesn't belong to logged in user", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Forbidden - Recipe doesn't belong to user\"}")
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Recipe not found in database", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Not Found - Recipe not found\"}")
            ))
    })
    public ResponseEntity<?> deleteRecipe(@PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null)
            return ResponseEntity.notFound().build();
        User user = userRepository.findById(session.getAttribute("sessionId").toString()).orElse(null);
        if (recipe != null && (recipe.getAuthor().equals(user.getId()) || user.isAdmin())) {
            recipeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own recipes.");
    }

    @GetMapping ("/{id}")
    @Operation(summary = "Get a single recipe", description = "Retrieve a single recipe based on its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipe", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Recipe not found in database", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Not Found - Recipe not found\"}")
            ))
    })
    public ResponseEntity<?> singleRecipe(@PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe != null) {
            Optional<User> userOptional = userRepository.findById(recipe.getAuthor());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
            return ResponseEntity.ok(recipe);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found.");
    }

    @PostMapping("/favorites/add")
    @Operation(summary = "Favorite a recipe", description = "Add a recipe to favorites")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully added recipe to favorites", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found in database", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Not Found - User not found\"}")
            ))
    })
    public ResponseEntity<?> addFavorite(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Recipe to favorite",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Recipe.class),
                    examples = @ExampleObject(
                            name = "SampleRecipe",
                            summary = "Example recipe JSON",
                            value = "{\n" +
                                    "  \"title\": \"Sushi\",\n" +
                                    "  \"description\": \"Simple homemade sushi with salmon and avocado.\",\n" +
                                    "  \"ingredients\": [\"sushi rice\", \"nori\", \"salmon\", \"avocado\"],\n" +
                                    "  \"instructions\": [\"Cook rice\", \"Place on nori\", \"Add fillings\", \"Roll and cut\"],\n" +
                                    "  \"cookingTime\": \"50\",\n" +
                                    "  \"category\": \"Main Dish\"\n" +
                                    "}"
                    )
            )
    ) @RequestBody Map<String, String> body, HttpServletRequest request) {
        String recipeId = body.get("recipeId");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = session.getAttribute("sessionId").toString();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return ResponseEntity.notFound().build();
        userOptional.ifPresent(user -> {
            if (!(user.getFavorites().contains(recipeId))) {
                user.getFavorites().add(recipeId);
                userRepository.save(user);
            }
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    @Operation(summary = "Retrieve favorite recipes", description = "Get favorite recipes of user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved favorite recipes", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found in database", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Not Found - User not found\"}")
            ))
    })
    public ResponseEntity<?> getFavorites(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in.");
        }

        String userId = session.getAttribute("sessionId").toString();
        Optional<User> userOptional = userRepository.findById(userId);

        if (((Optional<?>) userOptional).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        List<String> favoriteIds = userOptional.get().getFavorites();
        if (favoriteIds == null || favoriteIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<Recipe> favorites = recipeRepository.findByIdIn(favoriteIds);
        for (Recipe recipe : favorites)
        {
            Optional<User> userOption = userRepository.findById(recipe.getAuthor());
            if (userOption.isPresent()) {
                User user = userOption.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
        }
        return ResponseEntity.ok(favorites);

    }

    @DeleteMapping("/favorites/{id}")
    @Operation(summary = "Remove recipe from favorites", description = "Remove a recipe from favorites based on its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully removed recipe from favorites", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found in database", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Not Found - User not found\"}")
            ))
    })
    public ResponseEntity<?> removeFromFavorites(@PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = session.getAttribute("sessionId").toString();
        return userRepository.findById(userId).map(user -> {
            user.getFavorites().remove(id);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sortByTitleAsc")
    @Operation(summary = "Get recipes sorted by title ASC", description = "Retrieve all recipes sorted in ascending order based on title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipes", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity<?> sortByTitleAsc(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<Recipe> recipes = recipeRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
        for (Recipe recipe : recipes)
        {
            Optional<User> userOptional = userRepository.findById(recipe.getAuthor());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
        }
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/sortByTitleDesc")
    @Operation(summary = "Get recipes sorted by title DESC", description = "Retrieve all recipes sorted in descending order based on title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipes", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity<?> sortByTitleDesc(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<Recipe> recipes = recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "title"));
        for (Recipe recipe : recipes)
        {
            Optional<User> userOptional = userRepository.findById(recipe.getAuthor());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
        }
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/sortByTimeAsc")
    @Operation(summary = "Get recipes sorted by date ASC", description = "Retrieve all recipes sorted in ascending order based on date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipes", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity<List<Recipe>> sortByTimeAsc(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<Recipe> recipes = recipeRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"));
        for (Recipe recipe : recipes)
        {
            Optional<User> userOptional = userRepository.findById(recipe.getAuthor());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
        }
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/sortByTimeDesc")
    @Operation(summary = "Get recipes sorted by date DESC", description = "Retrieve all recipes sorted in descending order based on date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recipes", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Task has been accepted\"}")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized - User not logged in\"}")
            ))
    })
    public ResponseEntity<List<Recipe>> sortByTimeDesc(HttpServletRequest request) {
        List<Recipe> recipes = recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        for (Recipe recipe : recipes)
        {
            Optional<User> userOptional = userRepository.findById(recipe.getAuthor());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                recipe.setAuthor(user.getUsername());}
            else {
                recipe.setAuthor("Null");
            }
        }
        return ResponseEntity.ok(recipes);
    }

}
