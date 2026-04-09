package com.jackalcode.ecommerceapi.exceptions;

public class CartEmptyException extends RuntimeException {

    public CartEmptyException(String message) {
        super(message);
    }
}
