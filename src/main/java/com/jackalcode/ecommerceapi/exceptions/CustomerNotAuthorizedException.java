package com.jackalcode.ecommerceapi.exceptions;

public class CustomerNotAuthorizedException extends RuntimeException {

    public CustomerNotAuthorizedException(String message) {
        super(message);
    }
}
