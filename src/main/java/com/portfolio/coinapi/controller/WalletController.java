package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.service.WalletService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/V1-wallet")
public class WalletController {

    private final WalletService walletService;
    private final Logger walletControllerLogger;

    public WalletController(WalletService walletService, Logger walletControllerLogger) {
        this.walletService = walletService;
        this.walletControllerLogger = walletControllerLogger;
    }


    @GetMapping("/{walletId}")
    public ResponseEntity<Set<Coin>> listAllCoins(@Valid @PathVariable Long walletId) {
        walletControllerLogger.info("Received request to list all coins in wallet with id: {}", walletId);
        Set<Coin> coins = walletService.listCoinsByWalletId(walletId);
        walletControllerLogger.info("Found {} coins in wallet with id: {}", coins.size(), walletId);
        return ResponseEntity.status(HttpStatus.OK).body(coins);
    }


    @DeleteMapping("/{walletId}/coins/{coinId}")
    public ResponseEntity<?> removeCoin(@Valid @PathVariable Long walletId, @PathVariable Long coinId) {
        walletControllerLogger.info("Received request to remove coin with id {} from wallet with id: {}", coinId, walletId);
        walletService.removeCoin(walletId, coinId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/addCoin")
    public ResponseEntity<Wallet> addCoinToWallet(@Valid @RequestParam Long walletId, @RequestParam Long coinId) {
        walletControllerLogger.info("Received request to add coin with id {} to wallet with id: {}", coinId, walletId);
        Wallet updatedWallet = walletService.addCoin(walletId, coinId);
        walletControllerLogger.info("Coin with id {} added to wallet with id: {}", coinId, walletId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedWallet);
    }

}
