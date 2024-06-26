package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.repository.ClientRepository;
import com.portfolio.coinapi.service.AuthenticationService;
import com.portfolio.coinapi.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/authenticate")
public class AuthenticationController {
    private final RedisLogger logger;
    private final EmailService emailService;
    private final AuthenticationService authenticationService;
    private final ClientRepository clientRepository;

    public AuthenticationController(RedisLogger logger, EmailService emailService, AuthenticationService authenticationService,
                                    ClientRepository clientRepository) {
        this.logger = logger;
        this.emailService = emailService;
        this.authenticationService = authenticationService;
        this.clientRepository = clientRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(Authentication authentication) {
        logger.log("info", "Request to authenticate user: " + authentication.getName());
        try {
            String response = authenticationService.authenticate(authentication);
            logger.log("info", "User authenticated successfully: " + authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.log("error", "Authentication failed for user: " + authentication.getName() + ". Error: " + e.getMessage());
            return ResponseEntity.status(401).body("Authentication failed.");
        }
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateAccount(@Valid @PathVariable String token) {
        logger.log("info", "Request to activate account with token: " + token);
        Optional<Client> clientOptional = clientRepository.findByUniqueToken(token);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            authenticationService.activateAccount(client);
            emailService.sendWelcomeEmail(client.getEmail());
            logger.log("info", "Account activated successfully for token: " + token);
            return ResponseEntity.ok("Account activated successfully.");
        } else {
            logger.log("warn", "Invalid activation token: " + token);
            return ResponseEntity.badRequest().body("Invalid activation token.");
        }
    }
}
