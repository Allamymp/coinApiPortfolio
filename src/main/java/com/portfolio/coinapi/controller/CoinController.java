package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.DTO.CoinReturnDTO;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.service.CoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/v1/coin")
@CrossOrigin("*")
public class CoinController {

    private final RedisLogger logger;
    private final CoinService coinService;

    public CoinController(RedisLogger logger, CoinService coinService) {
        this.logger = logger;
        this.coinService = coinService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CoinReturnDTO>> getAll() {
        logger.log("info", "Request to list all coins");
        Page<CoinReturnDTO> page = coinService.listAll();
        logger.log("info", "Successfully requested coins to service, returned " + page.getTotalElements() + " coins.");
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CoinReturnDTO> findByName(@PathVariable String name) {
        logger.log("info", "Request to find coin by name: " + name);
        ResponseEntity<CoinReturnDTO> response = coinService.findByName(name);
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.log("info", "Successfully found coin: " + name);
        } else {
            logger.log("info", "Coin not found: " + name);
        }
        return response;
    }
}
