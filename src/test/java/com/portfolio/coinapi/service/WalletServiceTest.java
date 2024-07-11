package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.CoinRepository;
import com.portfolio.coinapi.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.portfolio.coinapi.commons.WalletConstants.WALLET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private RedisLogger walletServiceLogger;

    @InjectMocks
    private WalletService walletService;

    @Captor
    private ArgumentCaptor<Wallet> walletCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        WALLET.setId(1L);
    }

    @Test
    void create_ValidWallet_Success() {
        // Arrange
        Wallet wallet = WALLET;

        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Act
        Wallet createdWallet = walletService.create(wallet);

        // Assert
        assertNotNull(createdWallet);
        assertEquals(wallet.getId(), createdWallet.getId());

        // Verify logs
        verify(walletServiceLogger).log("info", "Creating wallet with ID: " + wallet.getId());
        verify(walletServiceLogger).log("info", "Wallet created successfully with ID: " + createdWallet.getId());
    }

    @Test
    void create_NullWallet_ExceptionThrown() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> walletService.create(null));
        assertEquals("Wallet object cannot be null", thrown.getMessage());

        // Verify logs
        verify(walletServiceLogger).log("warn", "Wallet object cannot be null");
    }

    @Test
    void listCoinsByWalletId_ExistingWallet_ReturnsListOfCoins() {
        // Arrange
        Wallet wallet = WALLET;
        Long walletId = wallet.getId();
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
        verify(walletServiceLogger).log("info", "Retrieving coins for wallet ID: " + walletId);
        verify(walletServiceLogger).log("info", "Number of coins retrieved for wallet ID " + walletId + ": " + coins.size());
    }

    @Test
    void listCoinsByWalletId_NonExistingWallet_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> walletService.listCoinsByWalletId(walletId));
        assertEquals("Wallet not found for id: 1", thrown.getMessage());

        // Verify logs
        verify(walletServiceLogger).log("info", "Retrieving coins for wallet ID: " + walletId);
        verify(walletServiceLogger).log("warn", "Wallet not found for id: " + walletId);
    }

    @Test
    void removeCoin_WalletNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> walletService.removeCoin(walletId, coinId));
        assertEquals("Wallet not found for id: 1", thrown.getMessage());

        // Verify logs
        verify(walletServiceLogger).log("info", "Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        verify(walletServiceLogger).log("warn", "Wallet not found for id: " + walletId);
    }

    @Test
    void removeCoin_CoinNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        Long walletId = wallet.getId();
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> walletService.removeCoin(walletId, coinId));
        assertEquals("Coin not found for id: 1", thrown.getMessage());

        // Verify logs
        verify(walletServiceLogger).log("info", "Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        verify(walletServiceLogger).log("warn", "Coin not found for id: " + coinId + " in wallet ID: " + walletId);
    }

    @Test
    void removeCoin_SuccessfullyRemovesCoin() {
        // Arrange
        Long coinId = 1L;
        Wallet wallet = WALLET;
        Long walletId = 1L;
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
        verify(walletServiceLogger).log("info", "Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        verify(walletServiceLogger).log("info", "Removed coin ID: " + coinId + " from wallet ID: " + walletId);
    }

    @Test
    void addCoin_WalletNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long walletId = 1L;
        Long coinId = 1L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> walletService.addCoin(walletId, coinId));
        assertEquals("Wallet not found for id: 1", thrown.getMessage());

        // Verify logs
        verify(walletServiceLogger).log("info", "Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        verify(walletServiceLogger).log("warn", "Wallet not found for id: " + walletId);
    }

    @Test
    void addCoin_CoinNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        Long coinId = 1L;
        Wallet wallet = WALLET;
        Long walletId = wallet.getId();
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(coinRepository.findById(coinId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> walletService.addCoin(walletId, coinId));
        assertEquals("Coin not found for id: 1", thrown.getMessage());

        // Verify logs
        verify(walletServiceLogger).log("info", "Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        verify(walletServiceLogger).log("warn", "Coin not found for id: " + coinId);
    }

    @Test
    void addCoin_CoinAlreadyPresentInWallet_ThrowsIllegalArgumentException() {
        // Arrange
        Long coinId = 1L;
        Wallet wallet = WALLET;
        Long walletId = wallet.getId();
        Coin coin = new Coin();
        coin.setId(coinId);
        Set<Coin> coinList = new HashSet<>();
        coinList.add(coin);
        wallet.setCoinList(coinList);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(coinRepository.findById(coinId)).thenReturn(Optional.of(coin));

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> walletService.addCoin(walletId, coinId));
        assertEquals("Coin is already present in the wallet", thrown.getMessage());

        // Verify logs
        verify(walletServiceLogger).log("info", "Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        verify(walletServiceLogger).log("warn", "Attempted to add duplicate coin ID: " + coinId + " to wallet ID: " + walletId);
    }

    @Test
    void addCoin_SuccessfullyAddsCoinToWallet() {
        // Arrange
        Long coinId = 1L;
        Wallet wallet = new Wallet();
        Long walletId = wallet.getId();
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
        verify(walletServiceLogger).log("info", "Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        verify(walletServiceLogger).log("info", "Added coin ID: " + coinId + " to wallet ID: " + walletId);
    }
}
