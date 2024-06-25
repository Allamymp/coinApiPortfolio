package com.portfolio.coinapi.service;


import com.portfolio.coinapi.infra.security.JwtService;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final ClientRepository clientRepository;


    public AuthenticationService(JwtService jwtService,
                                 ClientRepository clientRepository) {
        this.jwtService = jwtService;
        this.clientRepository = clientRepository;
    }

    public String authenticate(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }

    public void activateAccount(Client client) {
        client.setEnabled(true);
        String newToken = UUID.randomUUID().toString();
        client.setUniqueToken(newToken);
        clientRepository.save(client);
    }

}
