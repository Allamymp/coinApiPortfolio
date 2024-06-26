package com.portfolio.coinapi.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.portfolio.coinapi.config.log.RedisLogger;

@Service
public class EmailService {

    private static final String WELCOME_SUBJECT = "Welcome to Our Service";
    private static final String ACTIVATION_SUBJECT = "Account Activation";
    private static final String RESET_PASSWORD_SUBJECT = "Password Reset";
    private static final String NEW_PASSWORD_SUBJECT = "New Password";

    private final JavaMailSender emailSender;
    private final RedisLogger logger;

    public EmailService(JavaMailSender emailSender, RedisLogger logger) {
        this.emailSender = emailSender;
        this.logger = logger;
    }

    public void sendWelcomeEmail(String email) {
        logger.log("info", "Sending welcome email to: " + email);
        SimpleMailMessage message = createMessage(
                email,
                WELCOME_SUBJECT,
                "Dear " + email + ",\n\n"
                        + "Welcome to our service! Your account is successfully activated. We are excited to have you on board.\n\n"
                        + "Best regards,\n"
                        + "Your Service Team"
        );
        emailSender.send(message);
        logger.log("info", "Welcome email sent to: " + email);
    }


    public void sendActivationEmail(String email, String uniqueToken) {
        logger.log("info", "Sending activation email to: " + email);
        SimpleMailMessage message = createMessage(
                email,
                ACTIVATION_SUBJECT,
                "Dear User,\n\n"
                        + "Please click on the following link to activate your account:\n\n"
                        + linkBuilder(uniqueToken, "/activate/") + "\n\n"
                        + "Best regards,\n"
                        + "Your Service Team"
        );
        emailSender.send(message);
        logger.log("info", "Activation email sent to: " + email);
    }


    public void sendResetPasswordEmailAuth(String email, String uniqueToken) {
        logger.log("info", "Sending password reset email to: " + email);
        SimpleMailMessage message = createMessage(
                email,
                RESET_PASSWORD_SUBJECT,
                "Dear User,\n\n"
                        + "Please click on the following link to reset your password:\n\n"
                        + linkBuilder(uniqueToken, "/reset/") + "\n\n"
                        + "Best regards,\n"
                        + "Your Service Team"
        );
        emailSender.send(message);
        logger.log("info", "Password reset email sent to: " + email);
    }


    public void sendResetPasswordConfirmation(String email, String password) {
        logger.log("info", "Sending password reset confirmation email to: " + email);
        SimpleMailMessage message = createMessage(
                email,
                NEW_PASSWORD_SUBJECT,
                "Dear User,\n\n"
                        + "Your password has been successfully reset. Here is your new password:\n\n"
                        + password + "\n\n"
                        + "Please ensure to change your password after logging in for security purposes.\n\n"
                        + "Best regards,\n"
                        + "Your Service Team"
        );
        emailSender.send(message);
        logger.log("info", "Password reset confirmation email sent to: " + email);
    }


    private SimpleMailMessage createMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }


    private String linkBuilder(String uniqueToken, String endpoint) {
        return "http://localhost:8080" + endpoint + uniqueToken;
    }
}
