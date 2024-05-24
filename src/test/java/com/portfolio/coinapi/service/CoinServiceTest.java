package com.portfolio.coinapi.service;

import com.portfolio.coinapi.commons.CoinConstants;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.repository.CoinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.portfolio.coinapi.commons.CoinConstants.COIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoinServiceTest {

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private Logger coinServiceLogger;

    @InjectMocks
    private CoinService coinService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Captor
    private ArgumentCaptor<Coin> coinCaptor;

    @Test
    void createCoin_withValidData_Success() {
        // Arrange & Act
        coinService.createCoin(COIN);

        // Assert
        verify(coinRepository, times(1)).save(coinCaptor.capture());
        Coin capturedCoin = coinCaptor.getValue();
        assertNotNull(capturedCoin);
        assertEquals(COIN.getName(), capturedCoin.getName());
        assertEquals(COIN.getPrice(), capturedCoin.getPrice());
    }

    @Test
    void createCoin_withNullData_ThrowsException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> coinService.createCoin(null));
    }

    @Test
    void createCoin_logsCorrectly() {
        // Arrange & Act
        coinService.createCoin(COIN);

        // Assert
        verify(coinServiceLogger).info("CoinService: verifying if coin is not null");
        verify(coinServiceLogger).info("Creating coin with name: " + COIN.getName());
        verify(coinServiceLogger).info("Coin successfully created: " + COIN.getName());
    }

    @Test
    void updateCoin_withValidData_Success() {
        // Arrange
        Coin toSaveCoin = COIN;

        Coin savedCoin = COIN;
        savedCoin.setPrice(BigDecimal.valueOf(55000));
        savedCoin.setLast24hChange(BigDecimal.valueOf(0.06));
        savedCoin.setMarketValue(BigDecimal.valueOf(1.1));


        when(coinRepository.findByName(toSaveCoin.getName())).thenReturn(Optional.of(savedCoin));

        // Act
        coinService.updateCoin(toSaveCoin);

        // Assert
        assertEquals(toSaveCoin.getPrice(), savedCoin.getPrice());
        assertEquals(toSaveCoin.getLastUpdate(), savedCoin.getLastUpdate());
        assertEquals(toSaveCoin.getLast24hChange(), savedCoin.getLast24hChange());
        assertEquals(toSaveCoin.getMarketValue(), savedCoin.getMarketValue());
    }

    @Test
    void updateCoin_withNullData_ThrowsException() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> coinService.updateCoin(null));
    }

    @Test
    void updateCoin_whenCoinNotFound_ThrowsException() {
        // Arrange
        Coin toSaveCoin = CoinConstants.COIN;

        when(coinRepository.findByName(toSaveCoin.getName())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> coinService.updateCoin(toSaveCoin));
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
        verify(coinServiceLogger, times(1)).info("Listing all coins");
        verify(coinServiceLogger, times(1)).info("Total coins listed: 0");
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
        verify(coinServiceLogger, times(1)).info("Listing all coins");
        verify(coinServiceLogger, times(1)).info("Total coins listed: 1");
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
        verify(coinServiceLogger, times(1)).info("Listing all coins");
        verify(coinServiceLogger, times(1)).info("Total coins listed: 2");
    }
}
