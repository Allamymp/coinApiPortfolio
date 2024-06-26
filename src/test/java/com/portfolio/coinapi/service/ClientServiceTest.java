package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.portfolio.coinapi.commons.ClientConstants.CLIENT;
import static com.portfolio.coinapi.commons.ClientConstants.CLIENT_LIST;
import static com.portfolio.coinapi.commons.WalletConstants.WALLET;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/*
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private Logger clientServiceLogger;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

 /*   @Test
    void createClient_withValidData_returnsClient() {
        // Arrange

        when(clientRepository.existsByEmail(CLIENT.getEmail())).thenReturn(false);
        when(walletService.create(any(Wallet.class))).thenReturn(WALLET);
        when(clientRepository.save(any(Client.class))).thenReturn(CLIENT);

        // Act
        Client createdClient = clientService.create(CLIENT);

        // Assert
        assertNotNull(createdClient);
        assertEquals(CLIENT.getEmail(), createdClient.getEmail());
        assertEquals(CLIENT.getPassword(), createdClient.getPassword());
        assertNotNull(createdClient.getWallet());
        verify(clientRepository, times(1)).existsByEmail(CLIENT.getEmail());
        verify(clientRepository, times(1)).save(any(Client.class));
    } */
/*
    @Test
    void testCreateClient_DuplicatedUsernameException() {
        when(clientRepository.existsByEmail(CLIENT.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicatedUsernameException.class, () -> clientService.create(CLIENT));

        verify(clientRepository, times(1)).existsByEmail(CLIENT.getEmail());
        verify(clientRepository, times(0)).save(any(Client.class));
    }

    @Test
    void findById_withValidId_returnsClient() {
        // Arrange
        Long validId = 1L;
        Client expectedClient = CLIENT;

        when(clientRepository.findById(validId)).thenReturn(Optional.of(expectedClient));

        // Act
        Client sut = clientService.findById(validId).getBody();

        // Assert
        assertThat(sut).isEqualTo(expectedClient);
    }

    @Test
    void findById_withInvalidId_throwsException() {
        //Arrange
        Long invalidId = 999L;

        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> clientService.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void findByName_withValidName_returnsClient() {
        //Arrange
        String validName = CLIENT.getEmail();
        when(clientRepository.findByEmail(validName)).thenReturn(Optional.of(CLIENT));

        //Act
        Client sut = clientService.findByUsername(validName).getBody();

        //Assert
        assertThat(sut).isNotNull();
        assertThat(sut).isEqualTo(CLIENT);

    }

    @Test
    void findByName_withInvalidName_throwsException() {
        //Arrange
        String invalidName = "invalid_name";
        when(clientRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> clientService.findByUsername(invalidName)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void allClients_withValidPageable_returnsClientPage() {
        //Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Client> clientList = CLIENT_LIST;
        Page<Client> expectedPage = new PageImpl<>(clientList, pageable, clientList.size());
        when(clientRepository.findAll(pageable)).thenReturn(expectedPage);

        //Act
        Page<Client> sut = clientService.allClients(pageable);

        // Assert
        assertThat(sut).isEqualTo(expectedPage);
        assertThat(sut.getContent()).isEqualTo(clientList);
        assertThat(sut.getNumber()).isEqualTo(0);
        assertThat(sut.getSize()).isEqualTo(10);
    }

    @Test
    void allClients_withEmptyPage_returnsEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> expectedPage = Page.empty(pageable);

        when(clientRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Client> sut = clientService.allClients(pageable);

        // Assert
        assertThat(sut).isEqualTo(expectedPage);
        assertThat(sut.getContent().isEmpty()).isTrue();
        assertThat(sut.getNumber()).isEqualTo(0);
        assertThat(sut.getSize()).isEqualTo(10);
    }

    @Test
    void updateClient_withExistingClient_updatesClientSuccessfully() {
        // Arrange
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setEmail("oldUsername");
        existingClient.setPassword("oldPassword");

        Client updatedClient = new Client();
        updatedClient.setId(1L);
        updatedClient.setEmail("newUsername");
        updatedClient.setPassword("newPassword");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));

        // Act
        clientService.update(updatedClient);

        // Assert
        assertThat(existingClient.getEmail()).isEqualTo("newUsername");
        assertThat(existingClient.getPassword()).isEqualTo("newPassword");
        verify(clientRepository).save(existingClient);

    }


    @Test
    void updateClient_withNonExistingClient_throwsEntityNotFoundException() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        CLIENT.setId(1L);

        // Act & Assert
        assertThatThrownBy(() -> clientService.update(CLIENT))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Client not found for id: 1");

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void update_withNoChanges_doesNotUpdateClient() {
        // Arrange
        Client existingClient = CLIENT;
        existingClient.setId(1L);
        Client updatedClient = CLIENT;
        updatedClient.setId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));

        // Act
        clientService.update(updatedClient);

        // Assert
        assertThat(existingClient.getEmail()).isEqualTo(CLIENT.getEmail());
        assertThat(existingClient.getPassword()).isEqualTo(CLIENT.getPassword());
        verify(clientRepository).save(existingClient);
        verify(clientServiceLogger, never()).info("Username updated for client with ID: 1");
        verify(clientServiceLogger, never()).info("Password updated for client with ID: 1");
    }

    @Test
    void update_withPartialChanges_updatesOnlyChangedFields() {
        // Arrange
        Client existingClient = CLIENT;
        existingClient.setId(1L);
        Client updatedClient = CLIENT;
        updatedClient.setPassword("newPassword");
        updatedClient.setId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));

        // Act
        clientService.update(updatedClient);

        // Assert
        assertThat(existingClient.getEmail()).isEqualTo(CLIENT.getEmail());
        assertThat(existingClient.getPassword()).isEqualTo("newPassword");
        verify(clientRepository).save(existingClient);

    }

    @Test
    public void deleteById_withExistingClient_deletesClientSuccessfully() {
        // Arrange
        Long clientId = 1L;
        Client existingClient = new Client();
        existingClient.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));

        // Act
        clientService.deleteById(clientId);

        // Assert
        verify(clientRepository).delete(existingClient);
    }

    @Test
    public void deleteById_withNonexistentClient_throwsException() {
        // Arrange
        Long clientId = 1L;

            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Assert & act
        assertThatThrownBy(() -> clientService.deleteById(clientId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void deleteById_withExceptionDuringDelete_throwsException() {
        // Arrange
        Long clientId = 1L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clientService.deleteById(clientId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Client not found for id: " + clientId);

        verify(clientRepository, never()).delete(any(Client.class));
    }
}

*/