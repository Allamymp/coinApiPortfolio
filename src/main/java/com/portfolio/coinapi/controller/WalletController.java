package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }


    @GetMapping("/{walletId}")
    public ResponseEntity<List<Coin>> listAllCoins(@Valid @PathVariable Long walletId) {
        return ResponseEntity.status(HttpStatus.OK).body(walletService.listCoinsByWalletId(walletId));
    }

    @DeleteMapping("/{walletId}/coins/{coinId}")
    public ResponseEntity<?> removeCoin(@Valid @PathVariable Long walletId, @PathVariable Long coinId) {
        walletService.removeCoin(walletId,coinId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/addCoin")
    public ResponseEntity<Wallet> addCoinToWallet(@Valid @RequestParam Long walletId, @RequestParam Long coinId){
        return ResponseEntity.status(HttpStatus.OK).body(walletService.addCoin(walletId,coinId));
    }

}
