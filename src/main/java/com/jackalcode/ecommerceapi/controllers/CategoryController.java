package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.CreateCategoryRequest;
import com.jackalcode.ecommerceapi.entities.Category;
import com.jackalcode.ecommerceapi.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {

        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestBody CreateCategoryRequest createCategoryRequest) {

        return new ResponseEntity<>(categoryService.createCategory(
                createCategoryRequest), HttpStatus.CREATED );
    }
}
