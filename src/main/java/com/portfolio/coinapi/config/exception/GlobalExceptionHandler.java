package com.portfolio.coinapi.config.exception;

import com.portfolio.coinapi.config.log.RedisLogger;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final RedisLogger logger;

    public GlobalExceptionHandler(RedisLogger logger) {
        this.logger = logger;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        logger.log("error", "Unhandled exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An internal server error occurred. Please try again later."));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(EntityNotFoundException ex) {
        logger.log("warn", "Entity not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DuplicatedUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDuplicateUsernameException(DuplicatedUsernameException ex) {
        logger.log("warn", "Duplicated username: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.log("warn", "Validation exception: " + ex.getMessage());
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fieldError -> {
                            String defaultMessage = fieldError.getDefaultMessage();
                            return defaultMessage != null ? defaultMessage : "Invalid value";
                        }));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.log("warn", "Illegal argument: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid request. " + ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.log("error", "Data integrity violation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Data conflict. Please check your input and try again."));
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleTransactionSystemException(TransactionSystemException ex) {
        logger.log("error", "Transaction system exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Transaction system error. Please try again later."));
    }

    @ExceptionHandler(MailSendingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleMailSendingException(MailSendingException ex) {
        logger.log("error", "Mail sending exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to send email. Please try again later."));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.log("error", "Runtime exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred. Please try again later."));
    }

    // Classe para estruturar as respostas de erro
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
