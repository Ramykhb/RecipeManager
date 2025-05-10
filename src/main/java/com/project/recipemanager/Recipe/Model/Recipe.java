package com.project.recipemanager.Recipe.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "Recipes")
@Schema(description = "Recipe Model")
public class Recipe {
    @Id
    @Schema(example = "680a5bae7b06537d5a7389fe")
    private String id;

    @Schema(example = "Sushi")
    private String title;

    @Schema(example = "Delight in the simplicity and elegance of homemade sushi rolls...")
    private String description;

    @Schema(example = "[\"sushi rice\", \"nori sheets\", \"fresh salmon\", \"avocado\", \"cucumber\"]")
    private List<String> ingredients;

    @Schema(example = "[\"Prepare the sushi rice...\", \"Place a nori sheet...\", \"Roll tightly...\"]")
    private List<String> instructions;

    @Schema(example = "50")
    private String cookingTime;

    @Schema(example = "Main Dish")
    private String category;

    @Schema(example = "680610645c3f5c05c29324a7")
    private String author;

    @Schema(example = "2025-05-05")
    private String createdAt;

    @Schema(example = "https://media.istockphoto.com/id/1053854126/photo/all-you-can-eat-sushi.jpg?s=612x612&w=0&k=20&c=qqPJBYcxR0fgmzIFj_k2V6Mbo12hBBCucs1i5HcGYA0=")
    private String imageURL;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}
