package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.infra.security.JwtService;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {

    private final RedisLogger logger;
    private final JwtService jwtService;
    private final ClientRepository clientRepository;

    public AuthenticationService(RedisLogger logger, JwtService jwtService, ClientRepository clientRepository) {
        this.logger = logger;
        this.jwtService = jwtService;
        this.clientRepository = clientRepository;
    }

    public String authenticate(Authentication authentication) {
        logger.log("info", "Authenticating user: " + authentication.getName());
        String token = jwtService.generateToken(authentication);
        logger.log("info", "User authenticated successfully: " + authentication.getName());
        return token;
    }

    public void activateAccount(Client client) {
        logger.log("info", "Activating account for client: " + client.getEmail());
        client.setEnabled(true);
        String newToken = UUID.randomUUID().toString();
        client.setUniqueToken(newToken);
        clientRepository.save(client);
        logger.log("info", "Account activated successfully for client: " + client.getEmail());
    }
}
