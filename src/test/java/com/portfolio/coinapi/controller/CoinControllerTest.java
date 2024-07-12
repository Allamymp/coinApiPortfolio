package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.DTO.CoinReturnDTO;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CoinControllerTest {

    @Mock
    private CoinService coinService;

    @Mock
    private RedisLogger logger;

    @InjectMocks
    private CoinController coinController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_Success() {
        // Arrange
        CoinReturnDTO coinDTO = new CoinReturnDTO("1", "Bitcoin", BigDecimal.valueOf(40000), BigDecimal.valueOf(800000000), BigDecimal.valueOf(5), Instant.now());
        Page<CoinReturnDTO> coinPage = new PageImpl<>(Collections.singletonList(coinDTO));
        when(coinService.listAll()).thenReturn(coinPage);

        // Act
        ResponseEntity<Page<CoinReturnDTO>> response = coinController.getAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(logger).log("info", "Request to list all coins");
        verify(logger).log("info", "Successfully requested coins to service, returned 1 coins.");
    }

    @Test
    void findByName_Success() {
        // Arrange
        CoinReturnDTO coinDTO = new CoinReturnDTO("1", "Bitcoin", BigDecimal.valueOf(40000), BigDecimal.valueOf(800000000), BigDecimal.valueOf(5), Instant.now());
        when(coinService.findByName("Bitcoin")).thenReturn(new ResponseEntity<>(coinDTO, HttpStatus.OK));

        // Act
        ResponseEntity<CoinReturnDTO> response = coinController.findByName("Bitcoin");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bitcoin", response.getBody().name());
        verify(logger).log("info", "Request to find coin by name: Bitcoin");
        verify(logger).log("info", "Successfully found coin: Bitcoin");
    }

    @Test
    void findByName_NotFound() {
        // Arrange
        when(coinService.findByName("NonExistentCoin")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Act
        ResponseEntity<CoinReturnDTO> response = coinController.findByName("NonExistentCoin");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(logger).log("info", "Request to find coin by name: NonExistentCoin");
        verify(logger).log("info", "Coin not found: NonExistentCoin");
    }
}
