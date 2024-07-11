package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ClientServiceTest {

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private WalletService walletService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RedisLogger clientServiceLogger;

    @Autowired
    private ClientService clientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createClient_withValidData_returnsClientUri() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setPassword("Valid1Pass!");

        Wallet wallet = new Wallet();
        wallet.setId(1L);

        when(clientRepository.existsByEmail(client.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(client.getPassword())).thenReturn("EncodedPassword");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            savedClient.setId(1L);
            return savedClient;
        });
        when(walletService.create(any(Wallet.class))).thenReturn(wallet);

        // Act
        URI resultUri = clientService.create(client);

        // Assert
        URI expectedUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(1L)
                .toUri();

        assertEquals(expectedUri, resultUri);
    }

    @Test
    void createClient_withDuplicateEmail_throwsException() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setPassword("Valid1Pass!");

        when(clientRepository.existsByEmail(client.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicatedUsernameException.class, () -> clientService.create(client));
    }

    @Test
    void createClient_withInvalidPassword_throwsException() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setPassword("invalid");

        when(clientRepository.existsByEmail(client.getEmail())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clientService.create(client));
    }

    @Test
    void findById_withExistingClient_returnsClient() {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // Act
        ResponseEntity<Client> response = clientService.findById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(client, response.getBody());
    }

    @Test
    void findById_withNonExistingClient_returnsNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Client> response = clientService.findById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void findByUsername_withExistingClient_returnsClient() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(client));

        // Act
        ResponseEntity<Client> response = clientService.findByUsername("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(client, response.getBody());
    }

    @Test
    void findByUsername_withNonExistingClient_returnsNotFound() {
        // Arrange
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Client> response = clientService.findByUsername("test@example.com");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateClient_withExistingClient_updatesClient() {
        // Arrange
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setEmail("test@example.com");
        existingClient.setPassword("Valid1Pass!");

        Client updatedClient = new Client();
        updatedClient.setId(1L);
        updatedClient.setEmail("updated@example.com");
        updatedClient.setPassword("Updated1Pass!");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(passwordEncoder.encode(updatedClient.getPassword())).thenReturn("EncodedPassword");

        // Act
        clientService.updateClient(updatedClient);

        // Assert
        verify(clientRepository).save(existingClient);
        assertEquals("updated@example.com", existingClient.getEmail());
        assertEquals("EncodedPassword", existingClient.getPassword());
    }

    @Test
    void updateClient_withNonExistingClient_throwsException() {
        // Arrange
        Client updatedClient = new Client();
        updatedClient.setId(1L);
        updatedClient.setEmail("updated@example.com");
        updatedClient.setPassword("Updated1Pass!");

        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> clientService.updateClient(updatedClient));
    }

    @Test
    void deleteById_withExistingClient_deletesClient() {
        // Arrange
        Client client = new Client();
        client.setId(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // Act
        clientService.deleteById(1L);

        // Assert
        verify(clientRepository).delete(client);
    }

    @Test
    void deleteById_withNonExistingClient_throwsException() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> clientService.deleteById(1L));
    }

    @Test
    void resetPassword_withValidToken_resetsPassword() {
        // Arrange
        Client client = new Client();
        client.setUniqueToken("validToken");
        client.setEmail("test@example.com");

        when(clientRepository.findByUniqueToken("validToken")).thenReturn(Optional.of(client));
        when(passwordEncoder.encode(anyString())).thenReturn("EncodedPassword");

        // Act
        String[] result = clientService.resetPassword("validToken");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result[0]);
        assertEquals("EncodedPassword", client.getPassword());
    }

    @Test
    void resetPassword_withInvalidToken_returnsNull() {
        // Arrange
        when(clientRepository.findByUniqueToken("invalidToken")).thenReturn(Optional.empty());

        // Act
        String[] result = clientService.resetPassword("invalidToken");

        // Assert
        assertNull(result);
    }

    @Test
    void forgetPassword_withExistingEmail_sendsEmail() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setUniqueToken("uniqueToken");

        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(client));

        // Act
        clientService.forgetPassword("test@example.com");

        // Assert
        verify(emailService).sendResetPasswordEmailAuth("test@example.com", "uniqueToken");
    }

    @Test
    void forgetPassword_withNonExistingEmail_throwsException() {
        // Arrange
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clientService.forgetPassword("test@example.com"));
    }
}
