package com.project.recipemanager.Recipe.Repository;

import com.project.recipemanager.Recipe.Model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    Object findByTitleContainingIgnoreCase(String title);

    Object findByAuthor(String userId);

    List<Recipe> findByIdIn(List<String> ids);

}
