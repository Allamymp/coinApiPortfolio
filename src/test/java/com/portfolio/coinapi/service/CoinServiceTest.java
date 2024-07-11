package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.repository.CoinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoinServiceTest {

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private RedisLogger coinServiceLogger;

    @InjectMocks
    private CoinService coinService;

    @Captor
    private ArgumentCaptor<Coin> coinCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCoin_withValidData_Success() {
        // Arrange
        Coin coin = new Coin("Bitcoin", new BigDecimal("40000"), new BigDecimal("0.5"), new BigDecimal("800000000"), null);

        // Act
        coinService.createCoin(coin);

        // Assert
        verify(coinRepository, times(1)).save(coinCaptor.capture());
        Coin capturedCoin = coinCaptor.getValue();
        assertNotNull(capturedCoin);
        assertEquals(coin.getName(), capturedCoin.getName());
        assertEquals(coin.getPrice(), capturedCoin.getPrice());

        verify(coinServiceLogger).log("info", "Creating coin with name: " + coin.getName());
        verify(coinServiceLogger).log("info", "Coin successfully created: " + coin.getName());
    }

    @Test
    void createCoin_withNullData_ThrowsException() {
        // Act and Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> coinService.createCoin(null));
        assertEquals("Coin object cannot be null", thrown.getMessage());

        verify(coinServiceLogger).log("warn", "Coin object cannot be null");
    }

    @Test
    void updateCoin_withValidData_Success() {
        // Arrange
        Coin toSaveCoin = new Coin("Bitcoin", new BigDecimal("60000"), new BigDecimal("0.6"), new BigDecimal("1200000000"), null);
        Coin savedCoin = new Coin("Bitcoin", new BigDecimal("40000"), new BigDecimal("0.5"), new BigDecimal("800000000"), null);

        when(coinRepository.findByName(toSaveCoin.getName())).thenReturn(Optional.of(savedCoin));

        // Act
        coinService.updateCoin(toSaveCoin);

        // Assert
        assertEquals(toSaveCoin.getPrice(), savedCoin.getPrice());
        assertEquals(toSaveCoin.getLastUpdate(), savedCoin.getLastUpdate());
        assertEquals(toSaveCoin.getLast24hChange(), savedCoin.getLast24hChange());
        assertEquals(toSaveCoin.getMarketValue(), savedCoin.getMarketValue());

        verify(coinRepository).save(savedCoin);
        verify(coinServiceLogger).log("info", "Updating coin: " + toSaveCoin.getName());
        verify(coinServiceLogger).log("info", "Updating price for coin: " + savedCoin.getName());
        verify(coinServiceLogger).log("info", "Coin updated successfully: " + savedCoin.getName());
    }

    @Test
    void updateCoin_withNullData_ThrowsException() {
        // Act and Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> coinService.updateCoin(null));
        assertEquals("Coin object cannot be null", thrown.getMessage());

        verify(coinServiceLogger).log("warn", "Coin object cannot be null");
    }

    @Test
    void updateCoin_whenCoinNotFound_ThrowsException() {
        // Arrange
        Coin toSaveCoin = new Coin("NonExistentCoin", new BigDecimal("60000"), new BigDecimal("0.6"), new BigDecimal("1200000000"), null);

        when(coinRepository.findByName(toSaveCoin.getName())).thenReturn(Optional.empty());

        // Act and Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> coinService.updateCoin(toSaveCoin));
        assertEquals("Coin not found for name: NonExistentCoin", thrown.getMessage());

        verify(coinServiceLogger).log("warn", "Coin not found for name: " + toSaveCoin.getName());
    }

    @Test
    void listAll_withNoCoins_ReturnsEmptyList() {
        // Arrange
        List<Coin> emptyList = new ArrayList<>();
        when(coinRepository.findAll()).thenReturn(emptyList);

        // Act
        List<Coin> result = coinService.listAll();

        // Assert
        assertEquals(0, result.size());
        verify(coinRepository, times(1)).findAll();
        verify(coinServiceLogger, times(1)).log("info", "Listing all coins");
        verify(coinServiceLogger, times(1)).log("info", "Total coins listed: 0");
    }

    @Test
    void listAll_withSingleCoin_ReturnsListWithOneCoin() {
        // Arrange
        List<Coin> coinList = new ArrayList<>();
        Coin coin = new Coin();
        coinList.add(coin);

        when(coinRepository.findAll()).thenReturn(coinList);

        // Act
        List<Coin> result = coinService.listAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(coin, result.get(0));
        verify(coinRepository, times(1)).findAll();
        verify(coinServiceLogger, times(1)).log("info", "Listing all coins");
        verify(coinServiceLogger, times(1)).log("info", "Total coins listed: 1");
    }

    @Test
    void listAll_withMultipleCoins_ReturnsListWithMultipleCoins() {
        // Arrange
        List<Coin> coinList = new ArrayList<>();
        coinList.add(new Coin());
        coinList.add(new Coin());

        when(coinRepository.findAll()).thenReturn(coinList);

        // Act
        List<Coin> result = coinService.listAll();

        // Assert
        assertEquals(2, result.size());
        verify(coinRepository, times(1)).findAll();
        verify(coinServiceLogger, times(1)).log("info", "Listing all coins");
        verify(coinServiceLogger, times(1)).log("info", "Total coins listed: 2");
    }
}
