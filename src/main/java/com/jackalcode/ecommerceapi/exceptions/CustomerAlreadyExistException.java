package com.jackalcode.ecommerceapi.exceptions;

public class CustomerAlreadyExistException extends RuntimeException {

    public CustomerAlreadyExistException(String message) {
        super(message);
    }
}
