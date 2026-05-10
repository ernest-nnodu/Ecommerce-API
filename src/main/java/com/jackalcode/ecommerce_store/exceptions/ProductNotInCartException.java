package com.jackalcode.ecommerce_store.exceptions;

public class ProductNotInCartException extends RuntimeException {

    public ProductNotInCartException(String message) {
        super(message);
    }
}
