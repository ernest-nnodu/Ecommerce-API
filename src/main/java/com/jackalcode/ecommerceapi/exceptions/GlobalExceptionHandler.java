package com.jackalcode.ecommerceapi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCustomerNotFoundException(
            CustomerNotFoundException ex, HttpServletRequest request) {

        return new ApiError(ErrorCode.CUSTOMER_NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CustomerAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCustomerAlreadyExistException(
            CustomerAlreadyExistException ex, HttpServletRequest request) {

        return new ApiError(ErrorCode.CUSTOMER_ALREADY_EXISTS, ex.getMessage(), request.getRequestURI());
    }
}
