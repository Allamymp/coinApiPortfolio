package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.service.ClientService;
import com.portfolio.coinapi.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisLogger logger;

    @InjectMocks
    private ClientController clientController;

    @Captor
    private ArgumentCaptor<Client> clientCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createClient_Success() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        URI location = UriComponentsBuilder.fromPath("/v1/clients/1").build().toUri();
        when(clientService.create(any(Client.class))).thenReturn(location);

        // Act
        ResponseEntity<Void> response = clientController.create(client);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(location, response.getHeaders().getLocation());
        verify(logger).log("info", "Request to create new client: " + client.getEmail());
        verify(logger).log("info", "Client created successfully: " + client.getEmail());
    }

    @Test
    void findClientById_Success() {
        // Arrange
        Long clientId = 1L;
        Client client = new Client();
        client.setId(clientId);
        when(clientService.findById(clientId)).thenReturn(new ResponseEntity<>(client, HttpStatus.OK));

        // Act
        ResponseEntity<Client> response = clientController.findById(clientId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(client, response.getBody());
        verify(logger).log("info", "Request to find client by ID: " + clientId);
        verify(logger).log("info", "Client found: " + clientId);
    }

    @Test
    void findClientById_NotFound() {
        // Arrange
        Long clientId = 1L;
        when(clientService.findById(clientId)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Act
        ResponseEntity<Client> response = clientController.findById(clientId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(logger).log("info", "Request to find client by ID: " + clientId);
        verify(logger).log("warn", "Client not found: " + clientId);
    }

    @Test
    void findClientByUsername_Success() {
        // Arrange
        String username = "test@example.com";
        Client client = new Client();
        client.setEmail(username);
        when(clientService.findByUsername(username)).thenReturn(new ResponseEntity<>(client, HttpStatus.OK));

        // Act
        ResponseEntity<Client> response = clientController.findByUsername(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(client, response.getBody());
        verify(logger).log("info", "Request to find client by username: " + username);
        verify(logger).log("info", "Client found: " + username);
    }

    @Test
    void findClientByUsername_NotFound() {
        // Arrange
        String username = "test@example.com";
        when(clientService.findByUsername(username)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Act
        ResponseEntity<Client> response = clientController.findByUsername(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(logger).log("info", "Request to find client by username: " + username);
        verify(logger).log("warn", "Client not found: " + username);
    }

    @Test
    void findAllClients_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Client client = new Client();
        Page<Client> page = new PageImpl<>(Collections.singletonList(client), pageable, 1);
        when(clientService.allClients(pageable)).thenReturn(page);

        // Act
        ResponseEntity<Page<Client>> response = clientController.findAll(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(logger).log("info", "Request to find all clients");
        verify(logger).log("info", "Returning 1 clients");
    }

    @Test
    void updateClient_Success() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");

        // Act
        ResponseEntity<Void> response = clientController.update(client);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(clientService).updateClient(clientCaptor.capture());
        assertEquals(client.getEmail(), clientCaptor.getValue().getEmail());
        verify(logger).log("info", "Request to update client: " + client.getEmail());
        verify(logger).log("info", "Client updated successfully: " + client.getEmail());
    }

    @Test
    void deleteClient_Success() {
        // Arrange
        Long clientId = 1L;

        // Act
        ResponseEntity<Void> response = clientController.delete(clientId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(clientService).deleteById(clientId);
        verify(logger).log("info", "Request to delete client by ID: " + clientId);
        verify(logger).log("info", "Client deleted successfully: " + clientId);
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        String token = "validToken";
        String[] info = {"test@example.com", "newPassword"};
        when(clientService.resetPassword(token)).thenReturn(info);

        // Act
        ResponseEntity<?> response = clientController.resetPassword(token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New password sent to email!", response.getBody());
        verify(emailService).sendResetPasswordConfirmation(info[0], info[1]);
        verify(logger).log("info", "Request to reset password with token: " + token);
        verify(logger).log("info", "Password reset successfully for token: " + token);
    }

    @Test
    void resetPassword_InvalidToken() {
        // Arrange
        String token = "invalidToken";
        when(clientService.resetPassword(token)).thenReturn(null);

        // Act
        ResponseEntity<?> response = clientController.resetPassword(token);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid or expired token. Please request a new password reset link.", response.getBody());
        verify(logger).log("info", "Request to reset password with token: " + token);
        verify(logger).log("warn", "Invalid or expired token for password reset: " + token);
    }

    @Test
    void forgetPassword_Success() {
        // Arrange
        String email = "test@example.com";

        // Act
        ResponseEntity<?> response = clientController.forgetPassword(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password instructions sent to your email.", response.getBody());
        verify(clientService).forgetPassword(email);
        verify(logger).log("info", "Request to forget password for email: " + email);
        verify(logger).log("info", "Password reset instructions sent to email: " + email);
    }
}
