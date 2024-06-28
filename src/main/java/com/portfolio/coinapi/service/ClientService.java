package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final WalletService walletService;
    private final RedisLogger clientServiceLogger;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ClientService(ClientRepository clientRepository, WalletService walletService, RedisLogger redisLogger, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.clientRepository = clientRepository;
        this.walletService = walletService;
        this.clientServiceLogger = redisLogger;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public URI create(Client data) {
        String username = data.getEmail();
        if (clientRepository.existsByEmail(username)) {
            clientServiceLogger.log("info", "ClientService: User with username " + username + " already exists in database.");
            throw new DuplicatedUsernameException("Username already exists: " + username);
        }
        if (!validatePassword(data.getPassword())) {
            throw new IllegalArgumentException("A valid password must provide at least one uppercase"
                    + " letter, one lowercase letter, one special character, one number "
                    + "and be between 8 and 12 characters in length.");
        }
        clientServiceLogger.log("info", "Creating new client with username: " + username);

        Wallet wallet = new Wallet();
        clientServiceLogger.log("info", "Generating activation token");
        String activationToken = UUID.randomUUID().toString();
        data.setPassword(passwordEncoder.encode(data.getPassword()));
        data.setUniqueToken(activationToken);
        Client client = clientRepository.save(data);

        wallet.setClient(client);
        Wallet savedWallet = walletService.create(wallet);
        if (savedWallet == null) {
            throw new IllegalStateException("Failed to save wallet");
        }

        client.setWallet(savedWallet);
        clientRepository.save(client);

        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(client.getId())
                .toUri();
    }


    @Transactional(readOnly = true)
    public ResponseEntity<Client> findById(Long id) {
        clientServiceLogger.log("info", "Searching for client with ID: " + id);
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            clientServiceLogger.log("info", "Client found for id " + id + ":" + client);
            return ResponseEntity.status(HttpStatus.OK).body(client.get());
        } else {
            clientServiceLogger.log("info", "Client not found for id: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Client> findByUsername(String username) {
        clientServiceLogger.log("info", "Searching for client with username: " + username);
        Optional<Client> client = clientRepository.findByEmail(username);
        if (client.isPresent()) {
            clientServiceLogger.log("info", "Client found for username " + username + ": " + client);
            return ResponseEntity.status(HttpStatus.OK).body(client.get());
        } else {
            clientServiceLogger.log("info", "Client not found for username: " + username);
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional(readOnly = true)
    public Page<Client> allClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    @Transactional
    public void updateClient(Client updatedClient) {
        clientServiceLogger.log("info", "Updating client information for client with ID: " + updatedClient.getId());
        Client existingClient = clientRepository.findById(updatedClient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + updatedClient.getId()));

        existingClient.setEmail(updatedClient.getEmail());
        existingClient.setPassword(passwordEncoder.encode(updatedClient.getPassword()));

        clientRepository.save(existingClient);
    }

    public void forgetPassword(String email) {
        clientRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> emailService.sendResetPasswordEmailAuth(user.getEmail(), user.getUniqueToken()),
                        () -> {
                            throw new IllegalArgumentException("User not found for email: " + email);
                        }
                );
    }

    @Transactional
    public void deleteById(Long id) {
        clientServiceLogger.log("info", "Deleting client with ID: " + id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found for id: " + id));
        clientRepository.delete(client);
        clientServiceLogger.log("info", "Client with ID " + id + " deleted successfully.");
    }

    @Transactional
    public String[] resetPassword(String token) {
        Optional<Client> clientOptional = clientRepository.findByUniqueToken(token);
        if (clientOptional.isPresent()) {
            String newPassword = generateRandomPassword(8);
            Client user = clientOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUniqueToken(UUID.randomUUID().toString());
            clientRepository.save(user);
            return new String[]{user.getEmail(), newPassword};
        }
        return null;
    }


    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            newPassword.append(chars.charAt(randomIndex));
        }
        return newPassword.toString();
    }

    private boolean validatePassword(String password) {
        if (password.length() < 8 || password.length() > 12) {
            return false;
        }
        // verficia se senha corresponde parametros
        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern specialChar = Pattern.compile("[!@#$%^&*()-_+=<>?/{}\\[\\]]");
        Pattern numChar = Pattern.compile("[0-9]");

        Matcher upperCaseMatcher = upperCase.matcher(password);
        Matcher lowerCaseMatcher = lowerCase.matcher(password);
        Matcher specialCharMatcher = specialChar.matcher(password);
        Matcher numCharMatcher = numChar.matcher(password);

        return upperCaseMatcher.find() && lowerCaseMatcher.find()
                && specialCharMatcher.find() && numCharMatcher.find();
    }
}
