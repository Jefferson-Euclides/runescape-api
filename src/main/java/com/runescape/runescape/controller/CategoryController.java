package com.runescape.runescape.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.runescape.runescape.model.Category;
import com.runescape.runescape.model.Score;
import com.runescape.runescape.service.CategoryService;
import com.runescape.runescape.service.ScoreService;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ScoreService scoreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Category getCategoryById(
    		@PathVariable(value = "id") Integer id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Category insertCategory(
    		@RequestBody Category category) {
        return categoryService.insertCategory(category);
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void updateCategory(
    		@RequestBody Category category,
    		@PathVariable(value = "id") Integer id) {
        categoryService.updateCategory(category, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteCategory(
    		@PathVariable(value = "id") Integer id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping("/{id}/scores")
    @ResponseStatus(HttpStatus.OK)
    public List<Score> getScoresByCategoryId(
    		@PathVariable(value = "id") Integer id) {
        return scoreService.getScoresByCategoryId(id);
    }

}

