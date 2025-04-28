package com.project.recipemanager.Recipe.Service;

import com.project.recipemanager.User.Model.User;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Service
public class RecipeService {

    public String checkURL(String imageUrl, String fallbackUrl)
    {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 400) {
                return imageUrl;
            } else {
                return fallbackUrl;
            }
        } catch (Exception e) {
            return fallbackUrl;
        }
    }
}
