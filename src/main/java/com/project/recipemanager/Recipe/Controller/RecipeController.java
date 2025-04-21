package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping()
    public String allRecipes(HttpSession Session)
    {
        //TODO: Check if logged in: allow, if not redirect to login
        // all recipes
        return "all-recipes";
    }

    @GetMapping("/{id}")
    public String singleRecipes(HttpSession Session)
    {
        //TODO: Check if logged in: allow, if not redirect to login
        // single recipe
        return "single-recipe";
    }

    @GetMapping("/saved")
    public String savedRecipes(HttpSession Session)
    {
        //TODO: Check if logged in: allow, if not redirect to login
        // saved recipes
        return "saved-recipes";
    }

    @GetMapping("/my-posts")
    public String myRecipes(HttpSession Session)
    {
        //TODO: Check if logged in: allow, if not redirect to login
        // my posts
        return "my-recipes";
    }

    @GetMapping("/my-posts/{id}")
    public String editRecipe(HttpSession Session)
    {
        //TODO: Check if logged in: allow, if not redirect to login
        // edit post
        return "edit-recipe";
    }

    @GetMapping("/my-posts/new-post")
    public String addRecipes(HttpSession Session)
    {
        //TODO: Check if logged in: allow, if not redirect to login
        // new post
        return "add-recipe";
    }
}
