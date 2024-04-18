package com.portfolio.coinapi.config.exception;

public class DuplicatedUsernameException extends RuntimeException{

    public DuplicatedUsernameException(String message) {
        super(message);
    }
}
