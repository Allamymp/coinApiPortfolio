package com.portfolio.coinapi.init;

import com.portfolio.coinapi.config.init.InitializerCoinsByCoinGeckoApi;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.DTO.CoinReturnDTO;
import com.portfolio.coinapi.service.CoinGeckoService;
import com.portfolio.coinapi.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class InitializerCoinsByCoinGeckoApiTest {

    @Mock
    private CoinGeckoService coinGeckoService;

    @Mock
    private CoinService coinService;

    @Mock
    private RedisLogger logger;

    @InjectMocks
    private InitializerCoinsByCoinGeckoApi initializerCoinsByCoinGeckoApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void controlCoinsData_CoinsNotEmpty() {
        // Arrange
        CoinReturnDTO coinDTO = new CoinReturnDTO("1", "Bitcoin", BigDecimal.valueOf(40000), BigDecimal.valueOf(800000000), BigDecimal.valueOf(5), Instant.now());
        List<CoinReturnDTO> coinDTOList = List.of(coinDTO);
        Page<CoinReturnDTO> coinPage = new PageImpl<>(coinDTOList, PageRequest.of(0, coinDTOList.size()), coinDTOList.size());
        when(coinService.listAll()).thenReturn(coinPage);

        // Act
        initializerCoinsByCoinGeckoApi.controlCoinsData();

        // Assert
        verify(logger).log("info", "Initializing coin data...");
        verify(logger).log("info", "Updating coin data...");
        verify(coinGeckoService).fetchCoinDetails();
        verify(coinService, never()).createCoin(any(Coin.class));
    }

    @Test
    void controlCoinsData_CoinsEmpty() {
        // Arrange
        List<CoinReturnDTO> coinDTOList = Collections.emptyList();
        Page<CoinReturnDTO> coinPage = new PageImpl<>(coinDTOList, PageRequest.of(0, Math.max(1, coinDTOList.size())), coinDTOList.size());
        when(coinService.listAll()).thenReturn(coinPage);

        List<Coin> coins = List.of(new Coin(), new Coin());
        when(coinGeckoService.fetchCoinDetails()).thenReturn(coins);

        // Act
        initializerCoinsByCoinGeckoApi.controlCoinsData();

        // Assert
        verify(logger).log("info", "Initializing coin data...");
        verify(coinGeckoService).fetchCoinDetails();
        verify(coinService, times(2)).createCoin(any(Coin.class));
        verify(logger).log("info", "Coin data initialized successfully.");
    }

    @Test
    void controlCoinsData_Exception() {
        // Arrange
        when(coinService.listAll()).thenThrow(new RuntimeException("Test Exception"));

        // Act
        initializerCoinsByCoinGeckoApi.controlCoinsData();

        // Assert
        verify(logger).log(eq("warn"), startsWith("Error initializing coin data:"));
    }

    @Test
    void updateCoins_Success() {
        // Arrange
        List<Coin> coins = List.of(new Coin(), new Coin());
        when(coinGeckoService.fetchCoinDetails()).thenReturn(coins);

        // Act
        initializerCoinsByCoinGeckoApi.updateCoins();

        // Assert
        verify(logger).log("info", "Updating coin data...");
        verify(coinGeckoService).fetchCoinDetails();
        verify(coinService, times(2)).updateCoin(any(Coin.class));
        verify(logger).log("info", "Coin data updated successfully.");
    }

    @Test
    void updateCoins_Exception() {
        // Arrange
        when(coinGeckoService.fetchCoinDetails()).thenThrow(new RuntimeException("Test Exception"));

        // Act
        initializerCoinsByCoinGeckoApi.updateCoins();

        // Assert
        verify(logger).log(eq("warn"), startsWith("Error updating coin data:"));
    }
}
