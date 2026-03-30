package com.jackalcode.ecommerceapi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCategoryAlreadyExistException(CategoryAlreadyExistsException ex,
                                                        HttpServletRequest request) {
        return new ApiError(ErrorCode.CATEGORY_ALREADY_EXISTS, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryAlreadyExistException(CategoryNotFoundException ex,
                                                        HttpServletRequest request) {
        return new ApiError(ErrorCode.CATEGORY_NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleProductNotFoundException(ProductNotFoundException ex,
                                                   HttpServletRequest request) {
        return new ApiError(ErrorCode.PRODUCT_NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ProductAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleProductNotFoundException(ProductAlreadyExistException ex,
                                                   HttpServletRequest request) {
        return new ApiError(ErrorCode.PRODUCT_ALREADY_EXISTS, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {

        var errors = new HashMap<String, String>();

        ex.getBindingResult().getFieldErrors().forEach(
                (error) -> errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }
}
