package com.project.recipemanager.Recipe.Repository;

import com.project.recipemanager.Recipe.Model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> findByTitleContainingIgnoreCase(String title);

    List<Recipe> findByAuthor(String author);

    @Query("{ '_id': { $in: ?0 } }")
    List<Recipe> findByIdIn(List<String> ids);


}
