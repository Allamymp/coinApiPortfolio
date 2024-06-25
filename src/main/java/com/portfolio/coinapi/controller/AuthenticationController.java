package com.portfolio.coinapi.controller;



import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.repository.ClientRepository;
import com.portfolio.coinapi.service.AuthenticationService;
import com.portfolio.coinapi.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
public class AuthenticationController {
    private final EmailService emailService;
    private final AuthenticationService authenticationService;
    private final ClientRepository clientRepository;

    public AuthenticationController(EmailService emailService, AuthenticationService authenticationService,
                                    ClientRepository clientRepository) {
        this.emailService = emailService;
        this.authenticationService = authenticationService;
        this.clientRepository = clientRepository;
    }

    @PostMapping("/V1-authenticate")
    public String authenticate(Authentication authentication) {
        try {
            return authenticationService.authenticate(authentication);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateAccount(@Valid @PathVariable String token) {
        Optional<Client> clientOptional = clientRepository.findByUniqueToken(token);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            authenticationService.activateAccount(client);
            emailService.sendWelcomeEmail(client.getEmail());
            return ResponseEntity.ok("Account activated successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid activation token.");
    }

}