package com.project.recipemanager.Category.Repository;

import com.project.recipemanager.Category.Model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
}
