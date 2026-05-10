package com.jackalcode.ecommerce_store.exceptions;

public class CustomerNotAuthorizedException extends RuntimeException {

    public CustomerNotAuthorizedException(String message) {
        super(message);
    }
}
