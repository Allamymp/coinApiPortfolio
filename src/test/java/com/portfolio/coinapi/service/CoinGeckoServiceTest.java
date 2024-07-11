package com.portfolio.coinapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.coinapi.client.CoinGeckoClient;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoinGeckoServiceTest {

    @Mock
    private CoinGeckoClient coinGeckoClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisLogger coinGeckoServiceLogger;

    @InjectMocks
    private CoinGeckoService coinGeckoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchCoinDetails_ThrowsException_WhenJsonProcessingFails() throws JsonProcessingException {
        // Arrange
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Invalid JSON", HttpStatus.OK);
        when(coinGeckoClient.getCoinsDetailsRaw(anyString(), anyString(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(responseEntity);
        when(objectMapper.readTree(anyString())).thenThrow(JsonProcessingException.class);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> coinGeckoService.fetchCoinDetails());

        // Verify logs
        verify(coinGeckoServiceLogger).log(eq("info"), eq("Fetching coin details from CoinGecko."));
        verify(coinGeckoServiceLogger).log(eq("warn"), contains("Error processing JSON from CoinGecko"));
    }

    @Test
    void checkPingStatus_ReturnsTrue_WhenHttpStatusIsOK() {
        // Arrange
        when(coinGeckoClient.ping()).thenReturn(ResponseEntity.ok().build());

        // Act
        boolean pingStatus = coinGeckoService.checkPingStatus();

        // Assert
        assertTrue(pingStatus);

        // Verify logs
        verify(coinGeckoServiceLogger).log(eq("info"), eq("Checking CoinGecko ping status."));
        verify(coinGeckoServiceLogger).log(eq("info"), eq("Ping status received: OK"));
    }

    @Test
    void checkPingStatus_ReturnsFalse_WhenHttpStatusIsNotOK() {
        // Arrange
        when(coinGeckoClient.ping()).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act
        boolean pingStatus = coinGeckoService.checkPingStatus();

        // Assert
        assertFalse(pingStatus);

        // Verify logs
        verify(coinGeckoServiceLogger).log(eq("info"), eq("Checking CoinGecko ping status."));
        verify(coinGeckoServiceLogger).log(eq("info"), eq("Ping status received: NOT OK"));
    }

    @Test
    void getAllCoinIdsAsString_ReturnsCorrectString() {
        // Act
        String coinIds = coinGeckoService.getAllCoinIdsAsString();

        // Assert
        assertNotNull(coinIds);
        assertTrue(coinIds.contains("bitcoin"));
        assertTrue(coinIds.contains("ethereum"));
        assertTrue(coinIds.contains("solana"));

        // Verify logs
        verify(coinGeckoServiceLogger).log(eq("info"), eq("Getting all coin IDs as a comma-separated string."));
    }
}
