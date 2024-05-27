package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final WalletService walletService;
    private final Logger clientServiceLogger;

    public ClientService(ClientRepository clientRepository, WalletService walletService, Logger clientServiceLogger) {
        this.clientRepository = clientRepository;
        this.walletService = walletService;
        this.clientServiceLogger = clientServiceLogger;
    }

    @Transactional
    public Client create(Client data) {
        String username = data.getEmail();
        if (clientRepository.existsByUsername(username)) {
            clientServiceLogger.info("ClientService: User with username " + username + " already exists in database.");
            throw new DuplicatedUsernameException("Username already exists: " + username);
        }
        clientServiceLogger.info("Creating new client with username: " + username);

        Wallet wallet = new Wallet();
        Client client = clientRepository.save(data);
        wallet.setClient(client);
        client.setWallet(walletService.create(wallet));

        return clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Client> findById(Long id) {
        clientServiceLogger.info("Searching for client with ID: " + id);
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            clientServiceLogger.info("Client found for id {}: {}", id, client);
            return ResponseEntity.status(HttpStatus.OK).body(client.get());
        } else {
            clientServiceLogger.info("Client not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Client> findByUsername(String username) {
        clientServiceLogger.info("Searching for client with username: " + username);
        Optional<Client> client = clientRepository.findByUsername(username);
        if (client.isPresent()) {
            clientServiceLogger.info("Client found for username {}: {}", username, client);
            return ResponseEntity.status(HttpStatus.OK).body(client.get());
        } else {
            clientServiceLogger.info("Client not found for username: {}", username);
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional(readOnly = true)
    public Page<Client> allClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    @Transactional
    public void updateClient(Client updatedClient) {
        clientServiceLogger.info("Updating client information for client with ID: " + updatedClient.getId());
        Client existingClient = clientRepository.findById(updatedClient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + updatedClient.getId()));

        existingClient.setEmail(updatedClient.getEmail());
        existingClient.setPassword(updatedClient.getPassword());

        clientRepository.save(existingClient);
    }

    @Transactional
    public void deleteById(Long id) {
        clientServiceLogger.info("Deleting client with ID: " + id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + id));
        clientRepository.delete(client);
        clientServiceLogger.info("Client with ID " + id + " deleted successfully.");
    }
}
