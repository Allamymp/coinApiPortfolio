package com.portfolio.coinapi.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendWelcomeEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Welcome to Our Service");
        message.setText("Dear " + email + ",\n\n"
                + "Welcome to our service! Your account is successfully activated. We are excited to have you on board.\n\n"
                + "Best regards,\n"
                + "Your Service Team");
        emailSender.send(message);
    }

    public void sendActivationEmail(String email, String uniqueToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Account Activation");
        message.setText("Dear User,\n\n"
                + "Please click on the following link to activate your account:\n\n"
                + linkBuilder(uniqueToken,"/activate/") + "\n\n"
                + "Best regards,\n"
                + "Your Service Team");
        emailSender.send(message);
    }
    public void sendResetPasswordEmailAuth(String email, String uniqueToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText("Dear User,\n\n"
                + "Please click on the following link to reset your password:\n\n"
                + linkBuilder(uniqueToken,"/reset/") + "\n\n"
                + "Best regards,\n"
                + "Your Service Team");
        emailSender.send(message);
    }

    public void sendResetPasswordConfirmation(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("New Password");
        message.setText("Dear User,\n\n"
                + "Your password has been successfully reset. Here is your new password:\n\n"
                + password + "\n\n"
                + "Please ensure to change your password after logging in for security purposes.\n\n"
                + "Best regards,\n"
                + "Your Service Team");
        emailSender.send(message);
    }


    private String linkBuilder(String uniqueToken, String endpoint) {
        return "http://localhost:8080"+endpoint+ uniqueToken;
    }
}
