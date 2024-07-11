package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @Mock
    private RedisLogger logger;

    @InjectMocks
    private WalletController walletController;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @Captor
    private ArgumentCaptor<Wallet> walletCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAllCoins_Success() {
        // Arrange
        Long walletId = 1L;
        Set<Coin> coins = new HashSet<>();
        coins.add(new Coin());
        coins.add(new Coin());
        when(walletService.listCoinsByWalletId(walletId)).thenReturn(coins);

        // Act
        ResponseEntity<Set<Coin>> response = walletController.listAllCoins(walletId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coins, response.getBody());
        verify(logger).log("info", "Received request to list all coins in wallet with id: " + walletId);
        verify(logger).log("info", "Found " + coins.size() + " coins in wallet with id: " + walletId);
    }

    @Test
    void removeCoin_Success() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;

        // Act
        ResponseEntity<Void> response = walletController.removeCoin(walletId, coinId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(walletService).removeCoin(walletId, coinId);
        verify(logger).log("info", "Received request to remove coin with id " + coinId + " from wallet with id: " + walletId);
        verify(logger).log("info", "Coin with id " + coinId + " removed from wallet with id: " + walletId);
    }

    @Test
    void addCoinToWallet_Success() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        Coin coin = new Coin();
        coin.setId(coinId);
        wallet.setCoinList(new HashSet<>()); // Initialize the coin list

        when(walletService.addCoin(walletId, coinId)).thenAnswer(invocation -> {
            wallet.getCoinList().add(coin);
            return wallet;
        });

        // Act
        ResponseEntity<Wallet> response = walletController.addCoinToWallet(walletId, coinId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getCoinList().contains(coin));
        verify(walletService).addCoin(walletId, coinId);
        verify(logger).log("info", "Received request to add coin with id " + coinId + " to wallet with id: " + walletId);
        verify(logger).log("info", "Coin with id " + coinId + " added to wallet with id: " + walletId);
    }
}
