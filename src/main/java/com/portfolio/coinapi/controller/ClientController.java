package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.service.ClientService;
import com.portfolio.coinapi.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/clients")
@CrossOrigin("*")
public class ClientController {

    private final RedisLogger logger;

    private final ClientService clientService;
    private final EmailService emailService;

    public ClientController(RedisLogger logger, ClientService clientService, EmailService emailService) {
        this.logger = logger;
        this.clientService = clientService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Client client) {
        try {
            logger.log("info", "Request to create new client: " + client.getEmail());
            URI location = clientService.create(client);
            logger.log("info", "Client created successfully: " + client.getEmail());
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(@PathVariable String id) {
        try {
            logger.log("info", "Request to find client by ID: " + id);
            ResponseEntity<Client> response = clientService.findById(id);
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.log("info", "Client found: " + id);
            } else {
                logger.log("warn", "Client not found: " + id);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/username")
    public ResponseEntity<Client> findByUsername(@RequestParam String username) {
        logger.log("info", "Request to find client by username: " + username);
        ResponseEntity<Client> response = clientService.findByUsername(username);
        if (response.getStatusCode() == HttpStatus.OK) {
            logger.log("info", "Client found: " + username);
        } else {
            logger.log("warn", "Client not found: " + username);
        }
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Client>> findAll(@PageableDefault(sort = {"username"}) Pageable pageable) {
        logger.log("info", "Request to find all clients");
        Page<Client> clients = clientService.allClients(pageable);
        logger.log("info", "Returning " + clients.getTotalElements() + " clients");
        return ResponseEntity.ok().body(clients);
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody Client client) {
        logger.log("info", "Request to update client: " + client.getEmail());
        clientService.updateClient(client);
        logger.log("info", "Client updated successfully: " + client.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.log("info", "Request to delete client by ID: " + id);
        clientService.deleteById(id);
        logger.log("info", "Client deleted successfully: " + id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reset/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token) {
        logger.log("info", "Request to reset password with token: " + token);
        String[] info = clientService.resetPassword(token);
        if (info != null) {
            emailService.sendResetPasswordConfirmation(info[0], info[1]);
            logger.log("info", "Password reset successfully for token: " + token);
            return ResponseEntity.ok().body("New password sent to email!");
        } else {
            logger.log("warn", "Invalid or expired token for password reset: " + token);
            return ResponseEntity.badRequest().body("Invalid or expired token. Please request a new password reset link.");
        }
    }

    @GetMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestParam String email) {
        logger.log("info", "Request to forget password for email: " + email);
        clientService.forgetPassword(email);
        logger.log("info", "Password reset instructions sent to email: " + email);
        return ResponseEntity.ok().body("Password instructions sent to your email.");
    }
}
