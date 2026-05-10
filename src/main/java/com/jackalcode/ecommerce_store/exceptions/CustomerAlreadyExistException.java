package com.jackalcode.ecommerce_store.exceptions;

public class CustomerAlreadyExistException extends RuntimeException {

    public CustomerAlreadyExistException(String message) {
        super(message);
    }
}
