package com.project.recipemanager.User.Service;

import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public String login(String username, String password)
    {
        // TODO: HASH PASSWORDS
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getPassword().equals(password) ? user.getId() : "";
        } else {
            return "";
        }
    }
}
