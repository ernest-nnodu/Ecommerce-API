package com.jackalcode.ecommerceapi.configs;

import com.jackalcode.ecommerceapi.category.Category;
import com.jackalcode.ecommerceapi.customer.Customer;
import com.jackalcode.ecommerceapi.entities.*;
import com.jackalcode.ecommerceapi.repositories.CartRepository;
import com.jackalcode.ecommerceapi.category.CategoryRepository;
import com.jackalcode.ecommerceapi.customer.CustomerRepository;
import com.jackalcode.ecommerceapi.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final CartRepository cartRepo;

    private final Random random = new Random();
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        if (customerRepo.count() > 0) {
            System.out.println("Data already seeded");
            return;
        }

        //Insert customer admin
        var admin = new Customer();
        admin.setFirstName("Jack");
        admin.setLastName("Alice");
        admin.setEmail("jack.alice@mail.com");
        admin.setPassword(passwordEncoder.encode("AdminPassword1!"));
        admin.setRole(Role.ADMIN);
        customerRepo.save(admin);

        // =========================
        // 1. Customers (10)
        // =========================
        List<Customer> customers = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer();
            customer.setFirstName(randomFirstName());
            customer.setLastName(randomLastName());
            customer.setEmail(generateEmail(customer.getFirstName(), customer.getLastName()));
            customer.setPassword(passwordEncoder.encode("Abcd1234!"));
            customer.setRole(Role.USER);
            customers.add(customer);
        }

        customerRepo.saveAll(customers);

        //Assign cart to each customer
        customers.forEach(customer -> {
            Cart cart = new Cart();
            cart.setCustomer(customer);
            cartRepo.save(cart);
        });

        // =========================
        // 2. Categories (10)
        // =========================
        List<Category> categories = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Category category = new Category();
            category.setName(randomCategory());
            categories.add(category);
        }

        categories = categoryRepo.saveAll(categories);

        // =========================
        // 3. Products (100)
        // =========================
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Product product = new Product();

            product.setName(randomProductName());
            product.setQuantityInStock(randomQuantity());
            product.setPrice(randomPrice());

            // Assign random category
            Category randomCategory = categories.get(random.nextInt(categories.size()));
            product.setCategory(randomCategory);

            products.add(product);
        }

        productRepo.saveAll(products);

        System.out.println("✅ Seeded:");
        System.out.println(" - 10 customers");
        System.out.println(" - 10 categories");
        System.out.println(" - 100 products");
    }

    // =========================
    // Helper Methods
    // =========================

    private String randomFirstName() {
        String[] names = {"John", "Jane", "Michael", "Sarah", "David", "Emma", "Daniel", "Olivia", "James", "Sophia"};
        return names[random.nextInt(names.length)];
    }

    private String randomLastName() {
        String[] names = {"Smith", "Johnson", "Brown", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin"};
        return names[random.nextInt(names.length)];
    }

    private String generateEmail(String first, String last) {
        return first.toLowerCase() + "." + last.toLowerCase() + random.nextInt(1000) + "@mail.com";
    }

    private String randomCategory() {
        String[] categories = {
                "Electronics", "Books", "Clothing", "Home", "Toys",
                "Sports", "Beauty", "Automotive", "Garden", "Groceries"
        };
        return categories[random.nextInt(categories.length)];
    }

    private String randomProductName() {
        String[] products = {
                "Laptop", "Phone", "Headphones", "TV", "Camera",
                "Shoes", "Watch", "Backpack", "Keyboard", "Mouse"
        };
        return products[random.nextInt(products.length)] + "-" + random.nextInt(10000);
    }

    private Long randomQuantity() {
        return random.nextLong(1, 101);
    }

    private BigDecimal randomPrice() {
        double value = 10 + (200 - 10) * random.nextDouble();
        return BigDecimal.valueOf(Math.round(value * 100.0) / 100.0);
    }
}