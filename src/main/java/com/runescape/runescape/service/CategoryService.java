package com.runescape.runescape.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.runescape.runescape.exceptions.CategoryNotFoundException;
import com.runescape.runescape.exceptions.OverallCategoryCannotBeDeletedException;
import com.runescape.runescape.model.Category;
import com.runescape.runescape.repository.CategoryRepository;

@Service
public class CategoryService {

    Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        logger.info("Getting all categories");
        List<Category> categories = new ArrayList<>();
        
        categoryRepository.findAll().
                forEach(categories::add);
        return categories;
    }

    public Category getCategoryById(Integer id) {
        logger.info("Getting category with id: " + id);
        
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category insertCategory(Category category) {
        category = categoryRepository.save(category);
        
        //logger.info("Inserted category with name: " + category.getId());
        return category;
    }

    public void updateCategory(Category category, Integer id) {
        Category tempCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        tempCategory.setName(category.getName());
        categoryRepository.save(tempCategory);
        logger.info("Updated category with id: " + id);
    }

    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        if (id == 1) {
            throw new OverallCategoryCannotBeDeletedException();
        }
        categoryRepository.deleteById(id);
        logger.info("Deleted category with id: " + id);
    }

}
