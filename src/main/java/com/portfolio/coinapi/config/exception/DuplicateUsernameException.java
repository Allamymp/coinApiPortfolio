package com.portfolio.coinapi.config.exception;

public class DuplicateUsernameException extends RuntimeException{

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
