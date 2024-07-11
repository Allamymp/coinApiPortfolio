package com.portfolio.coinapi.exception;

import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.config.exception.GlobalExceptionHandler;
import com.portfolio.coinapi.config.exception.MailSendingException;
import com.portfolio.coinapi.config.log.RedisLogger;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class GlobalExceptionHandlerTest {

    @Mock
    private RedisLogger redisLogger;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleException() {
        Exception ex = new Exception("Test exception");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An internal server error occurred. Please try again later.", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("error", "Unhandled exception: Test exception");
    }

    @Test
    void handleNoSuchElementException() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleNoSuchElementException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("warn", "Entity not found: Entity not found");
    }

    @Test
    void handleDuplicateUsernameException() {
        DuplicatedUsernameException ex = new DuplicatedUsernameException("Duplicated username");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleDuplicateUsernameException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Duplicated username", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("warn", "Duplicated username: Duplicated username");
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request. Invalid argument", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("warn", "Illegal argument: Invalid argument");
    }

    @Test
    void handleDataIntegrityViolationException() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data integrity violation");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Data conflict. Please check your input and try again.", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("error", "Data integrity violation: Data integrity violation");
    }

    @Test
    void handleTransactionSystemException() {
        TransactionSystemException ex = new TransactionSystemException("Transaction system error");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleTransactionSystemException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Transaction system error. Please try again later.", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("error", "Transaction system exception: Transaction system error");
    }

    @Test
    void handleMailSendingException() {
        MailSendingException ex = new MailSendingException("Failed to send email");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleMailSendingException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to send email. Please try again later.", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("error", "Mail sending exception: Failed to send email");
    }

    @Test
    void handleRuntimeException() {
        RuntimeException ex = new RuntimeException("Runtime exception");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred. Please try again later.", Objects.requireNonNull(response.getBody()).getMessage());
        verify(redisLogger).log("error", "Runtime exception: Runtime exception");
    }
}
