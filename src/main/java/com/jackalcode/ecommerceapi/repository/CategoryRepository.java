package com.jackalcode.ecommerceapi.repository;

import com.jackalcode.ecommerceapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}