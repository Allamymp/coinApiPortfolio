package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.log.RedisLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private RedisLogger logger;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendWelcomeEmail_SendsEmail_Success() {
        // Arrange
        String email = "test@example.com";

        // Act
        emailService.sendWelcomeEmail(email);

        // Assert
        verify(emailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Welcome to Our Service", message.getSubject());
        assertEquals(
                "Dear test@example.com,\n\nWelcome to our service! Your account is successfully activated. We are excited to have you on board.\n\nBest regards,\nYour Service Team",
                message.getText()
        );
        verify(logger).log("info", "Sending welcome email to: " + email);
        verify(logger).log("info", "Welcome email sent to: " + email);
    }

    @Test
    void sendActivationEmail_SendsEmail_Success() {
        // Arrange
        String email = "test@example.com";
        String token = "uniqueToken123";

        // Act
        emailService.sendActivationEmail(email, token);

        // Assert
        verify(emailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Account Activation", message.getSubject());
        assertEquals(
                "Dear User,\n\nPlease click on the following link to activate your account:\n\nhttp://localhost:8080/activate/" + token + "\n\nBest regards,\nYour Service Team",
                message.getText()
        );
        verify(logger).log("info", "Sending activation email to: " + email);
        verify(logger).log("info", "Activation email sent to: " + email);
    }

    @Test
    void sendResetPasswordEmailAuth_SendsEmail_Success() {
        // Arrange
        String email = "test@example.com";
        String token = "uniqueToken123";

        // Act
        emailService.sendResetPasswordEmailAuth(email, token);

        // Assert
        verify(emailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("Password Reset", message.getSubject());
        assertEquals(
                "Dear User,\n\nPlease click on the following link to reset your password:\n\nhttp://localhost:8080/reset/" + token + "\n\nBest regards,\nYour Service Team",
                message.getText()
        );
        verify(logger).log("info", "Sending password reset email to: " + email);
        verify(logger).log("info", "Password reset email sent to: " + email);
    }

    @Test
    void sendResetPasswordConfirmation_SendsEmail_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "newPassword123";

        // Act
        emailService.sendResetPasswordConfirmation(email, password);

        // Assert
        verify(emailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(email, message.getTo()[0]);
        assertEquals("New Password", message.getSubject());
        assertEquals(
                "Dear User,\n\nYour password has been successfully reset. Here is your new password:\n\n" + password + "\n\nPlease ensure to change your password after logging in for security purposes.\n\nBest regards,\nYour Service Team",
                message.getText()
        );
        verify(logger).log("info", "Sending password reset confirmation email to: " + email);
        verify(logger).log("info", "Password reset confirmation email sent to: " + email);
    }
}
