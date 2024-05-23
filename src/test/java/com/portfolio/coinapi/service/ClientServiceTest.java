package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createClient_withValidData_returnsClient() {
        // Arrange

        when(clientRepository.existsByUsername(CLIENT.getUsername())).thenReturn(false);
        when(walletService.create(any(Wallet.class))).thenReturn(WALLET);
        when(clientRepository.save(any(Client.class))).thenReturn(CLIENT);

        // Act
        Client createdClient = clientService.create(CLIENT);

        // Assert
        assertNotNull(createdClient);
        assertEquals(CLIENT.getUsername(), createdClient.getUsername());
        assertEquals(CLIENT.getPassword(), createdClient.getPassword());
        assertNotNull(createdClient.getWallet());
        verify(clientRepository, times(1)).existsByUsername(CLIENT.getUsername());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testCreateClient_DuplicatedUsernameException() {
        when(clientRepository.existsByUsername(CLIENT.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicatedUsernameException.class, () -> clientService.create(CLIENT));

        verify(clientRepository, times(1)).existsByUsername(CLIENT.getUsername());
        verify(clientRepository, times(0)).save(any(Client.class));
    }

    @Test
    void findById_withValidId_returnsClient() {
        // Arrange
        Long validId = 1L;
        Client expectedClient = CLIENT;

        when(clientRepository.findById(validId)).thenReturn(Optional.of(expectedClient));

        // Act
        Client sut = clientService.findById(validId);

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
        String validName = CLIENT.getUsername();
        when(clientRepository.findByUsername(validName)).thenReturn(Optional.of(CLIENT));

        //Act
        Client sut = clientService.findByUsername(validName);

        //Assert
        assertThat(sut).isNotNull();
        assertThat(sut).isEqualTo(CLIENT);

    }

    @Test
    void findByName_withInvalidName_throwsException() {
        //Arrange
        String invalidName = "invalid_name";
        when(clientRepository.findByUsername(anyString())).thenReturn(Optional.empty());

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

    //faltando update e delete

}
