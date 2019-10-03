package com.runescape.runescape.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.runescape.runescape.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    Optional<Category> findByName(String name);

    Optional<Category> findById(Integer id);

}

