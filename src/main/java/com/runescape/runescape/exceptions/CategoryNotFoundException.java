package com.runescape.runescape.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such category")
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Integer id) {
        super("Category with id : " + id + " not found");
    }
    
    public CategoryNotFoundException() {
        super("Category not found");
    }

}
