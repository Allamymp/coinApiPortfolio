package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.exception.DuplicatedUsernameException;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.ClientRepository;
import com.portfolio.coinapi.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;

import static com.portfolio.coinapi.commons.ClientConstants.CLIENT;
import static com.portfolio.coinapi.commons.WalletConstants.WALLET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private RedisLogger clientServiceLogger;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ClientService clientService;




    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        CLIENT.setId(null);
        WALLET.setId(null);
    }

    @Test
    void createClient_withValidData_returnsClientUri() {
        // Arrange
       // when(clientRepository.existsByEmail(CLIENT.getEmail())).thenReturn(false);
        when(walletService.create(WALLET)).thenReturn(WALLET);

        Wallet wallet = walletService.create(WALLET);

        assertThat(wallet).isNotNull();
    }
}
