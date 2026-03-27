package com.jackalcode.ecommerceapi.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return id == cartItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CartItem{" + "id=" + id +
                ", quantity=" + quantity +
                ", cart=" + cart.getId() +
                ", product=" + product.getName() +
                '}';
    }
}
