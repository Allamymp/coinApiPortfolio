package com.portfolio.coinapi.service;


import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final WalletService walletService;

    private static final Logger clientServiceLogger = LogManager.getLogger(ClientService.class);

    public ClientService(ClientRepository clientRepository, WalletService walletService) {
        this.clientRepository = clientRepository;
        this.walletService = walletService;
    }

    @Transactional
    public Client create(Client client) {
        String username = client.getUsername();
        if (clientRepository.existsByUsername(username)) {
            clientServiceLogger.info("ClientService: Verifying if user with id "
                    + username + " already exists in database.");
            throw new DuplicatedUsernameException("Username already exists: " + username);
        }
        clientServiceLogger.info("Creating new client with username: " + username);
        return clientRepository.save(new Client(
                client.getUsername(),
                client.getPassword(),
                walletService.create(new Wallet())));
    }


    public Client findById(Long id) {
        clientServiceLogger.info("Searching for client with ID: " + id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + id));

    }

    public Client findByUsername(String username) {
        clientServiceLogger.info("Searching for client with username: " + username);
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for username: " + username));
    }

    public Page<Client> allClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    @Transactional
    public void update(Client client) {
        clientServiceLogger.info("Updating client information for client with ID: " + client.getId());
        Client clientToSave = clientRepository.findById(client.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + client.getId()));
        if (!clientToSave.getUsername().equals(client.getUsername())) {
            clientToSave.setUsername(client.getUsername());
            clientServiceLogger.info("Username updated for client with ID: " + client.getId());
        }
        if (!clientToSave.getPassword().equals(client.getPassword())) {
            clientToSave.setPassword(client.getPassword());
            clientServiceLogger.info("Password updated for client with ID: " + client.getId());
        }
        clientRepository.save(clientToSave);
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
