package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/v1/wallet")
@CrossOrigin("*")
public class WalletController {

    private final WalletService walletService;
    private final RedisLogger logger;

    public WalletController(WalletService walletService, RedisLogger logger) {
        this.walletService = walletService;
        this.logger = logger;
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Set<Coin>> listAllCoins(@PathVariable Long walletId) {
        logger.log("info", "Received request to list all coins in wallet with id: " + walletId);
        Set<Coin> coins = walletService.listCoinsByWalletId(walletId);
        logger.log("info", "Found " + coins.size() + " coins in wallet with id: " + walletId);
        return ResponseEntity.status(HttpStatus.OK).body(coins);
    }

    @DeleteMapping("/{walletId}/coins/{coinId}")
    public ResponseEntity<Void> removeCoin(@PathVariable Long walletId, @PathVariable Long coinId) {
        logger.log("info", "Received request to remove coin with id " + coinId + " from wallet with id: " + walletId);
        walletService.removeCoin(walletId, coinId);
        logger.log("info", "Coin with id " + coinId + " removed from wallet with id: " + walletId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{walletId}/coins")
    public ResponseEntity<Wallet> addCoinToWallet(@PathVariable Long walletId, @RequestParam Long coinId) {
        logger.log("info", "Received request to add coin with id " + coinId + " to wallet with id: " + walletId);
        Wallet updatedWallet = walletService.addCoin(walletId, coinId);
        logger.log("info", "Coin with id " + coinId + " added to wallet with id: " + walletId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedWallet);
    }

}
