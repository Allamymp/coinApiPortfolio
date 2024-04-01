package com.portfolio.coinapi.service;


import com.portfolio.coinapi.config.exception.DuplicateUsernameException;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final WalletService walletService;

    public ClientService(ClientRepository clientRepository, WalletService walletService) {
        this.clientRepository = clientRepository;
        this.walletService = walletService;
    }


    public Client create(Client client) {

        if (clientRepository.existsByUsername(client.getUsername())) {
            throw new DuplicateUsernameException("Username already exists: " + client.getUsername());
        }
        Wallet wallet = new Wallet();
        client.setWallet(wallet);
        walletService.create(wallet);
        return clientRepository.save(client);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + id));

    }

    public Client findByUsername(String username) {
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for username: " + username));
    }

    public List<Client> allClients() {
        return clientRepository.findAll();
    }

    public void update(Client client) {
        Client clientToSave = clientRepository.findById(client.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + client.getId()));
        if (!clientToSave.getUsername().equals(client.getUsername())) {
            clientToSave.setUsername(client.getUsername());
        }
        if (!clientToSave.getPassword().equals(client.getPassword())) {
            clientToSave.setPassword(client.getPassword());
        }
        clientRepository.save(clientToSave);
    }

    public void deleteById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + id));
        clientRepository.delete(client);
    }
}
