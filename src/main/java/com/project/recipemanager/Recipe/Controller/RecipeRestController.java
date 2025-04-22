package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Model.Recipe;
import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    @Autowired
    private RecipeRepository recipeRepository;

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
}
