package com.portfolio.coinapi.service;


import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.CoinRepository;
import com.portfolio.coinapi.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.apache.logging.log4j.Logger;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.portfolio.coinapi.commons.WalletConstants.WALLET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private Logger walletServiceLogger;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ValidWallet_Success() {
        // Arrange
        Wallet wallet = WALLET;
        wallet.setId(1L);

        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Act
        Wallet createdWallet = walletService.create(wallet);

        // Assert
        assertNotNull(createdWallet);
        assertEquals(wallet.getId(), createdWallet.getId());

        // Verify logs
        verify(walletServiceLogger).info("Created wallet with ID: " + wallet.getId());
    }

    @Test
    void create_NullWallet_ExceptionThrown() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> walletService.create(null));

        // Verify logs
        verify(walletServiceLogger).error("Failed to create wallet: wallet object is null.");
    }

    @Test
    void listCoinsByWalletId_ExistingWallet_ReturnsListOfCoins() {
        // Arrange
        Long walletId = 1L;
        Wallet wallet = new Wallet();
        Set<Coin> coinList = new HashSet<>();
        coinList.add(new Coin());
        coinList.add(new Coin());
        wallet.setCoinList(coinList);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act
        Set<Coin> coins = walletService.listCoinsByWalletId(walletId);

        // Assert
        assertNotNull(coins);
        assertEquals(coinList.size(), coins.size());

        // Verify logs
        verify(walletServiceLogger).info("Retrieving coins for wallet ID: " + walletId);
        verify(walletServiceLogger).info("Number of coins retrieved for wallet ID " + walletId + ": " + coins.size());
    }

    @Test
    void listCoinsByWalletId_NonExistingWallet_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> walletService.listCoinsByWalletId(walletId));

        // Verify logs
        verify(walletServiceLogger).info("Retrieving coins for wallet ID: " + walletId);
        verify(walletServiceLogger).error("Wallet not found for ID: " + walletId);
    }

    @Test
    void removeCoin_WalletNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> walletService.removeCoin(walletId, coinId));

        // Verify logs
        verify(walletServiceLogger).info("Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        verify(walletServiceLogger).error("Wallet not found for ID: " + walletId);
    }

    @Test
    void removeCoin_CoinNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> walletService.removeCoin(walletId, coinId));

        // Verify logs
        verify(walletServiceLogger).info("Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        verify(walletServiceLogger).error("Coin not found for ID: " + coinId + " in wallet ID: " + walletId);
    }

    @Test
    void removeCoin_SuccessfullyRemovesCoin() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        Coin coin = new Coin();
        coin.setId(coinId);
        Set<Coin> coinList = new HashSet<>();
        coinList.add(coin);
        wallet.setCoinList(coinList);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act
        walletService.removeCoin(walletId, coinId);

        // Assert
        assertFalse(wallet.getCoinList().contains(coin));

        // Verify logs
        verify(walletServiceLogger).info("Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        verify(walletServiceLogger).info("Removed coin ID: " + coinId + " from wallet ID: " + walletId);
    }

    @Test
    void addCoin_WalletNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> walletService.addCoin(walletId, coinId));

        // Verify logs
        verify(walletServiceLogger).info("Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        verify(walletServiceLogger).error("Wallet not found for ID: " + walletId);
    }

    @Test
    void addCoin_CoinNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(coinRepository.findById(coinId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> walletService.addCoin(walletId, coinId));

        // Verify logs
        verify(walletServiceLogger).info("Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        verify(walletServiceLogger).error("Coin not found for ID: " + coinId);
    }

    @Test
    void addCoin_CoinAlreadyPresentInWallet_ThrowsIllegalArgumentException() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        Coin coin = new Coin();
        coin.setId(coinId);
        Set<Coin> coinList = new HashSet<>();
        coinList.add(coin);
        wallet.setCoinList(coinList);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(coinRepository.findById(coinId)).thenReturn(Optional.of(coin));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> walletService.addCoin(walletId, coinId));

        // Verify logs
        verify(walletServiceLogger).info("Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        verify(walletServiceLogger).warn("Attempted to add duplicate coin ID: " + coinId + " to wallet ID: " + walletId);
    }

    @Test
    void addCoin_SuccessfullyAddsCoinToWallet() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        Coin coin = new Coin();
        coin.setId(coinId);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(coinRepository.findById(coinId)).thenReturn(Optional.of(coin));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Act
        Wallet updatedWallet = walletService.addCoin(walletId, coinId);

        // Assert
        assertTrue(updatedWallet.getCoinList().contains(coin));

        // Verify logs
        verify(walletServiceLogger).info("Adding coin ID: " + coinId + " to wallet ID: " + walletId);
    }
}