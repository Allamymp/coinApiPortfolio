package com.portfolio.coinapi.repository;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.portfolio.coinapi.commons.ClientConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    public void beforeEach() {

    }

    @AfterEach
    public void afterEach() {
        CLIENT.setId(null);
        CLIENT.setUniqueToken(UUID.randomUUID().toString());
        CLIENT.setWallet(new Wallet());
        CLIENT.setEmail("clientName@email.com");
        CLIENT.setPassword("cltPassword");
    }

    @Test
    public void createClient_withValidData_returnsClient() {
        // Arrange & Act
        Client client = clientRepository.save(CLIENT);

        // Assert
        Client sut = testEntityManager.find(Client.class, client.getId());
        assertThat(sut).isNotNull();
        assertThat(sut.getEmail()).isEqualTo(CLIENT.getEmail());
        assertThat(sut.getPassword()).isEqualTo(CLIENT.getPassword());
    }

    @Test
    @Transactional
    public void createClient_withInvalidData_ThrowsException() {
        // Arrange

        // Act & Assert
        assertThatThrownBy(() -> clientRepository.save(EMPTY_CLIENT)).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> clientRepository.save(INVALID_CLIENT)).isInstanceOf(Exception.class);
    }

    @Test
    @Transactional
    public void getClient_byExistingEmail_returnsClient() {
        // Arrange
        Client client = clientRepository.save(CLIENT);

        // Act
        Optional<Client> sut = clientRepository.findByEmail(CLIENT.getEmail());

        // Assert
        assertThat(sut).isPresent();
        assertThat(sut.get().getEmail()).isEqualTo(client.getEmail());
        assertThat(sut.get().getPassword()).isEqualTo(client.getPassword());
    }

    @Test
    @Transactional
    public void getClient_byUnexistingEmail_returnsEmpty() {
        // Arrange

        // Act
        Optional<Client> sut = clientRepository.findByEmail(CLIENT.getEmail());

        // Assert
        assertThat(sut).isEmpty();
    }

    @Test
    @Transactional
    public void getClient_byExistingId_returnsClient() {
        // Arrange
        Client client = clientRepository.save(CLIENT);

        // Act
        Optional<Client> sut = clientRepository.findById(client.getId());

        // Assert
        assertThat(sut).isPresent();
        assertThat(sut.get().getId()).isEqualTo(client.getId());
        assertThat(sut.get().getEmail()).isEqualTo(client.getEmail());
        assertThat(sut.get().getPassword()).isEqualTo(client.getPassword());
    }

    @Test
    @Transactional
    public void getClient_byUnexistingId_returnsEmpty() {
        // Arrange

        // Act
        Optional<Client> sut = clientRepository.findById(1L);

        // Assert
        assertThat(sut).isEmpty();
    }

    @Test
    @Transactional
    public void listClients_returnsClientList() {
        // Arrange
        CLIENT_LIST.forEach(testEntityManager::persistAndFlush);

        // Act
        List<Client> sut = clientRepository.findAll();

        // Assert
        assertThat(sut).hasSize(3);
        assertThat(sut.get(0).getEmail()).isEqualTo("firstClient@email.com");
        assertThat(sut.get(1).getEmail()).isEqualTo("secondClient@email.com");
        assertThat(sut.get(2).getEmail()).isEqualTo("thirdClient@email.com");
    }

    @Test
    @Transactional
    public void listClients_returnsNoClients() {
        // Arrange

        // Act
        List<Client> sut = clientRepository.findAll();

        // Assert
        assertThat(sut).isEmpty();
    }

    @Test
    @Transactional
    public void updateClient_withValidData_returnsUpdatedClient() {
        // Arrange
        Client client = clientRepository.save(CLIENT);
        String newEmail = "newEmail@example.com";
        String newPassword = "newPassword";

        // Act
        client.setEmail(newEmail);
        client.setPassword(newPassword);
        clientRepository.save(client);

        // Assert
        Client sut = testEntityManager.find(Client.class, client.getId());
        assertThat(sut).isNotNull();
        assertThat(sut.getEmail()).isEqualTo(newEmail);
        assertThat(sut.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @Transactional
    public void updateClient_withInvalidData_ThrowsException() {
        // Arrange
        Client client = clientRepository.save(CLIENT);

        // Act & Assert
        client.setEmail(null);
        assertThatThrownBy(() -> clientRepository.save(client)).isInstanceOf(Exception.class);

        client.setEmail("invalidEmail");
        client.setPassword(null);
        assertThatThrownBy(() -> clientRepository.save(client)).isInstanceOf(Exception.class);
    }

    @Test
    @Transactional
    public void deleteClient_byExistingId_deletesClient() {
        // Arrange
      testEntityManager.persist(CLIENT);

        // Act
        clientRepository.deleteById(CLIENT.getId());
        Optional<Client> sut = clientRepository.findById(CLIENT.getId());

        // Assert
        assertThat(sut).isEmpty();
    }

    @Test
    @Transactional
    public void deleteClient_byUnexistingId_doesNothing() {
        // Arrange
        long nonExistentId = 999L;

        // Act
        clientRepository.deleteById(nonExistentId);

        // Assert
        // No exception should be thrown, nothing to assert
    }
}
