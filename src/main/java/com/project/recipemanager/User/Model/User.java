package com.project.recipemanager.User.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Users")
@Data
@Schema(description = "User Model")
public class User {
    @Id
    @Schema(example = "680a5bae7b06537d5a7389fe")
    private String id;

    @Schema(example = "ahmad")
    private String username;

    @Schema(example = "ahmadassaf")
    private String password;

    @Schema(example = "true")
    private boolean isAdmin;

    @Schema(example = "[\"680a5bae7b06537d5f7389fe\", \"440a5bae7b06537d5a7322er\", \"4fff5bae7b06537d5a73211f\", \"440a5bae7b06537d5a73ga2g\"]")
    private List<String> favorites=new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<String> getFavorites() {
        return favorites;
    }

}
