package com.project.recipemanager.User.Service;

import com.project.recipemanager.User.Model.User;
import com.project.recipemanager.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public String login(String username, String password)
    {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user.getId();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
    public String signup(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return "";
        } else {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(hashedPassword);
            userRepository.save(newUser);
            return newUser.getId();
        }
    }

    public void addToFavorites(String userId, String recipeId) {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.ifPresent(user -> {
            if (!(user.getFavorites().contains(recipeId))) {
                user.getFavorites().add(recipeId);
                userRepository.save(user);
            }
        });
    }


}
