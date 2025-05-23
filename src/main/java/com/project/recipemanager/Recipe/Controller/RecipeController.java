package com.project.recipemanager.Recipe.Controller;

import com.project.recipemanager.Recipe.Model.Recipe;
import com.project.recipemanager.Recipe.Repository.RecipeRepository;
import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.Optional;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public String allRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
        {
            User user = userRepository.findById(session.getAttribute("sessionId").toString()).orElse(null);
            if (user.isAdmin())
                return "all-recipes-admin";
            return "all-recipes";
        }
        return "redirect:/users/login";
    }

    @GetMapping("/{id}")
    public String singleRecipes(HttpServletRequest request, @PathVariable String id)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
        {
            Optional<Recipe> recipeOpt = recipeRepository.findById(id);
            if (recipeOpt.isPresent())
                return "single-recipe";
            else
                return "redirect:/users/login";
        }
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
    public String myRecipes(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null) {
            return "redirect:/users/login";
        }
        String userId = (String) session.getAttribute("sessionId");
        model.addAttribute("userId", userId);
        return "my-recipes";
    }

    @GetMapping("/my-posts/new-post")
    public String addRecipes(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
            return "add-recipe";
        return "redirect:/users/login";
    }

    @GetMapping("/my-posts/{id}")
    public String editRecipes(HttpServletRequest request, @PathVariable String id)
    {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("sessionId") != null)
        {
            Optional<Recipe> recipeOpt = recipeRepository.findById(id);
            if (recipeOpt.isPresent())
                return "edit-recipe";
            else
                return "redirect:/users/login";
        }
        return "redirect:/users/login";
    }
}
