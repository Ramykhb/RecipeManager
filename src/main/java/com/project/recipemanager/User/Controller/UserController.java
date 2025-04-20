package com.project.recipemanager.User.Controller;

import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping("/login")
    public String getLoginPage(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null)
            System.out.println(session.getAttribute("sessionId").toString());
        return "login";
    }

}
