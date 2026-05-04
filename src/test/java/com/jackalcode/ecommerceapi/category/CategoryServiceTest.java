package com.jackalcode.ecommerceapi.category;

import com.jackalcode.ecommerceapi.exceptions.CategoryAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("getCategories: returns list when categories exist")
    void getCategories_returnsListOfCategories() {

        Category c1 = new Category();
        c1.setId(1L);
        c1.setName("Electronics");

        Category c2 = new Category();
        c2.setId(2L);
        c2.setName("Books");

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Category> results = categoryService.getCategories();

        assertAll(
                () -> assertNotNull(results),
                () -> assertEquals(2, results.size(), "Expected two categories"),
                () -> assertEquals(1L, results.get(0).getId()),
                () -> assertEquals("Electronics", results.get(0).getName()),
                () -> assertEquals(2L, results.get(1).getId()),
                () -> assertEquals("Books", results.get(1).getName())
        );

        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("getCategories: returns empty list when there are no categories")
    void getCategories_whenNone_returnsEmptyList() {

        when(categoryRepository.findAll()).thenReturn(List.of());

        List<Category> results = categoryService.getCategories();

        assertAll(
                () -> assertNotNull(results),
                () -> assertTrue(results.isEmpty(), "Expected empty category list")
        );

        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("createCategory: saves and returns category when name does not already exist")
    void createCategory_withValidDetails_savesAndReturnsCategory() {

        CategoryRequest request = new CategoryRequest("Sports");

        Category mapped = new Category();
        mapped.setName("Sports");

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Sports");

        when(categoryRepository.existsByNameIgnoreCase("Sports")).thenReturn(false);
        when(categoryMapper.toCategory(request)).thenReturn(mapped);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        Category result = categoryService.createCategory(request);
        System.out.println(result.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("Sports", result.getName())
        );

        verify(categoryRepository).existsByNameIgnoreCase("Sports");
        verify(categoryMapper).toCategory(request);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("createCategory: throws CategoryAlreadyExistsException when category name already exists")
    void createCategory_whenNameExists_throwsException() {

        CategoryRequest request = new CategoryRequest("Electronics");

        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.createCategory(request));

        verify(categoryRepository).existsByNameIgnoreCase("Electronics");
        verify(categoryMapper, never()).toCategory(any());
        verify(categoryRepository, never()).save(any());
    }
}
