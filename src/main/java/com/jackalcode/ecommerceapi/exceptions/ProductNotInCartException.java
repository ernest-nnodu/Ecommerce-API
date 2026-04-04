package com.jackalcode.ecommerceapi.exceptions;

public class ProductNotInCartException extends RuntimeException {

    public ProductNotInCartException(String message) {
        super(message);
    }
}
