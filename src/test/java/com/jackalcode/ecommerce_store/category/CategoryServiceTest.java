package com.jackalcode.ecommerce_store.category;

import com.jackalcode.ecommerce_store.exceptions.CategoryAlreadyExistsException;
import com.jackalcode.ecommerce_store.exceptions.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper);
    }

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
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        Category result = categoryService.createCategory(request);
        System.out.println(result.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("Sports", result.getName())
        );

        verify(categoryRepository).existsByNameIgnoreCase("Sports");
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
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateCategory: updates existing category when valid")
    void updateCategory_withValidDetails_updatesCategory() {

        Long id = 1L;
        Category existing = new Category();
        existing.setId(id);
        existing.setName("OldName");

        CategoryRequest request = new CategoryRequest("Home");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameIgnoreCase("Home")).thenReturn(false);

        Category result = categoryService.updateCategory(id, request);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(id, result.getId()),
                () -> assertEquals("Home", result.getName())
        );

        verify(categoryRepository).findById(id);
        verify(categoryRepository).existsByNameIgnoreCase("Home");
    }

    @Test
    @DisplayName("updateCategory: throws CategoryNotFoundException when id does not exist")
    void updateCategory_withInvalidID_throwsException() {

        Long id = 999L;
        CategoryRequest request = new CategoryRequest("Home");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(id, request));

        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).existsByNameIgnoreCase(anyString());
    }

    @Test
    @DisplayName("updateCategory: throws CategoryAlreadyExistsException when new name already exists")
    void updateCategory_whenNameExists_throwsException() {

        Long id = 1L;
        Category existing = new Category();
        existing.setId(id);
        existing.setName("OldName");

        CategoryRequest request = new CategoryRequest("Electronics");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.updateCategory(id, request));

        verify(categoryRepository).findById(id);
        verify(categoryRepository).existsByNameIgnoreCase("Electronics");
    }
}
