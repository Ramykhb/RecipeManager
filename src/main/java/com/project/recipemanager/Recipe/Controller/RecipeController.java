package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Model.Recipe;
import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping
    public ResponseEntity<List<Recipe>> getRecipes()
    {
        return ResponseEntity.ok(recipeRepository.findAll());
    }

    @PostMapping
    public ResponseEntity addRecipe(@RequestBody Recipe recipe)
    {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(dateFormatter);
        recipe.setCreatedAt(formattedDate);
        recipeRepository.save(recipe);
        return ResponseEntity.ok().build();
    }
}
