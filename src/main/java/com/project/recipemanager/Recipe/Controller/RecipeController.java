package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    public String allRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return "all-recipes";
        return "redirect:/users/login";
    }

    @GetMapping("/{id}")
    public String singleRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return "single-recipe";
        return "redirect:/users/login";
    }

    @GetMapping("/saved")
    public String savedRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return "saved-recipes";
        return "redirect:/users/login";
    }

    @GetMapping("/my-posts")
    public String myRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return "my-recipes";
        return "redirect:/users/login";
    }

    @GetMapping("/my-posts/{id}")
    public String editRecipe(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return "edit-recipe";
        return "redirect:/users/login";
    }

    @GetMapping("/my-posts/new-post")
    public String addRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return "add-recipe";
        return "redirect:/users/login";
    }
}
