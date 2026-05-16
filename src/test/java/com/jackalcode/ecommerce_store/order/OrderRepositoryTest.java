package com.jackalcode.ecommerce_store.order;

import com.jackalcode.ecommerce_store.category.Category;
import com.jackalcode.ecommerce_store.customer.Customer;
import com.jackalcode.ecommerce_store.product.Product;
import com.jackalcode.ecommerce_store.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findAllByCustomerId should return only orders for the matching customer")
    void findAllByCustomerId_whenCustomerHasOrders_returnsOnlyCustomerOrders() {
        var customer = createCustomer("buyer@example.com");
        var otherCustomer = createCustomer("other@example.com");
        var product = createProduct();

        persistToDatabase(customer);
        persistToDatabase(otherCustomer);
        persistToDatabase(product.getCategory());
        persistToDatabase(product);

        var firstOrder = createOrder(customer, product, 2);
        var secondOrder = createOrder(customer, product, 1);
        var otherCustomerOrder = createOrder(otherCustomer, product, 3);

        persistToDatabase(firstOrder);
        persistToDatabase(secondOrder);
        persistToDatabase(otherCustomerOrder);

        var orders = orderRepository.findAllByCustomerId(customer.getId());

        assertAll(
                () -> assertEquals(2, orders.size()),
                () -> assertTrue(orders.stream()
                        .allMatch(order -> order.getCustomer().getId().equals(customer.getId()))),
                () -> assertTrue(orders.stream()
                        .anyMatch(order -> order.getId().equals(firstOrder.getId()))),
                () -> assertTrue(orders.stream()
                        .anyMatch(order -> order.getId().equals(secondOrder.getId()))),
                () -> assertFalse(orders.stream()
                        .anyMatch(order -> order.getId().equals(otherCustomerOrder.getId())))
        );
    }

    @Test
    @DisplayName("findAllByCustomerId should return empty list when customer has no orders")
    void findAllByCustomerId_whenCustomerHasNoOrders_returnsEmptyList() {
        var customer = createCustomer("buyer@example.com");

        persistToDatabase(customer);

        var orders = orderRepository.findAllByCustomerId(customer.getId());

        assertTrue(orders.isEmpty());
    }

    @Test
    @DisplayName("save should persist order items with the order")
    void save_whenOrderHasItems_persistsOrderItems() {
        var customer = createCustomer("buyer@example.com");
        var product = createProduct();

        persistToDatabase(customer);
        persistToDatabase(product.getCategory());
        persistToDatabase(product);

        var order = createOrder(customer, product, 2);

        var savedOrder = orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        var foundOrder = orderRepository.findById(savedOrder.getId());

        assertAll(
                () -> assertTrue(foundOrder.isPresent()),
                () -> assertEquals(customer.getId(), foundOrder.get().getCustomer().getId()),
                () -> assertEquals(OrderStatus.PENDING, foundOrder.get().getStatus()),
                () -> assertEquals(new BigDecimal("29.98"), foundOrder.get().getTotalAmount()),
                () -> assertEquals(1, foundOrder.get().getOrderItems().size()),
                () -> assertEquals(product.getId(),
                        foundOrder.get().getOrderItems().getFirst().getProduct().getId())
        );
    }

    private void persistToDatabase(Object entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
    }

    private Order createOrder(Customer customer, Product product, int quantity) {
        var order = new Order();
        order.setCustomer(customer);
        order.setDate(Instant.now());
        order.setStatus(OrderStatus.PENDING);

        var orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());
        orderItem.setQuantity(quantity);

        order.addOrderItem(orderItem);
        order.calculateTotalAmount();

        return order;
    }

    private Customer createCustomer(String email) {
        return Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();
    }

    private Product createProduct() {
        var category = new Category();
        category.setName("Electronics");

        var product = new Product();
        product.setName("Headphones");
        product.setPrice(new BigDecimal("14.99"));
        product.setQuantityInStock(10L);
        product.setCategory(category);

        return product;
    }
}
