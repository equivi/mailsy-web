package com.equivi.mailsy.listener.exception;

/**
 * Created by eldridaditya on 6/9/15.
 */
public class InvalidMailsyAppException extends RuntimeException {
    public InvalidMailsyAppException(String errorMessage) {
        super(errorMessage);
    }
}
