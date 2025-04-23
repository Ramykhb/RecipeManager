package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Model.Recipe;
import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public ResponseEntity<List<Recipe>> getRecipes()
    {
        return ResponseEntity.ok(recipeRepository.findAll());
    }

    @PostMapping
    public ResponseEntity addRecipe(@RequestBody Recipe recipe, HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(dateFormatter);
        recipe.setCreatedAt(formattedDate);
        recipe.setAuthor(session.getAttribute("sessionId").toString());
        recipeRepository.save(recipe);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok((List<Recipe>) recipeRepository.findByTitleContainingIgnoreCase(title));
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<Recipe>> getMyRecipes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String userId = session.getAttribute("sessionId").toString();
        return ResponseEntity.ok((List<Recipe>) recipeRepository.findByAuthor(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe, HttpServletRequest request) {
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
            recipeRepository.save(recipe);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String userId = session.getAttribute("sessionId").toString();
        return recipeRepository.findById(id).map(recipe -> {
            if (!recipe.getAuthor().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            recipeRepository.delete(recipe);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/favorites/{id}")
    public ResponseEntity<?> addToFavorites(@PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = session.getAttribute("sessionId").toString();
        return userRepository.findById(userId).map(user -> {
            user.getFavorites().add(id);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/favorites")
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
        return ResponseEntity.ok(favorites);
    }


    @DeleteMapping("/favorites/{id}")
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
}
