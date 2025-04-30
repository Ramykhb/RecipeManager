package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Model.Recipe;
import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import com.project.recipemanager.Recipe.Service.RecipeService;
import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import com.project.recipemanager.User.Service.UserService;
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
    public ResponseEntity<List<Recipe>> getRecipes()
    {
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
        recipe.setImageURL(recipeService.checkURL(recipe.getImageURL(), "https://www.cezarskitchen.com.my/wp-content/themes/consultix/images/no-image-found-360x250.png"));
        recipeRepository.save(recipe);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchByTitle(@RequestParam String title) {
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
    public ResponseEntity<List<Recipe>> getMyRecipes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String userId = session.getAttribute("sessionId").toString();
        return ResponseEntity.ok((List<Recipe>) recipeRepository.findByAuthor(userId));
    }

    @PutMapping("my-posts/{id}")
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
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        User user = userRepository.findById(session.getAttribute("sessionId").toString()).orElse(null);
        if (recipe != null && (recipe.getAuthor().equals(user.getId()) || user.isAdmin())) {
            recipeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own recipes.");
    }

    @GetMapping ("/{id}")
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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Recipe not found.");
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
    public ResponseEntity<List<Recipe>> sortByTitleAsc() {
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
    public ResponseEntity<List<Recipe>> sortByTitleDesc() {
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
    public ResponseEntity<List<Recipe>> sortByTimeAsc() {
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
    public ResponseEntity<List<Recipe>> sortByTimeDesc() {
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
