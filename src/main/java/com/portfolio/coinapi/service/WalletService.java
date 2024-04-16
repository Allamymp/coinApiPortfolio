package com.portfolio.coinapi.service;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.CoinRepository;
import com.portfolio.coinapi.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;

    public WalletService(WalletRepository walletRepository,
                         CoinRepository coinRepository) {
        this.walletRepository = walletRepository;
        this.coinRepository = coinRepository;
    }

    public void create(Wallet wallet) {
        walletRepository.save(wallet);
    }

    public List<Coin> listCoinsByWalletId(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for id: " + walletId));
        return new ArrayList<>(wallet.getCoinList());
    }


    public void removeCoin(Long walletId, Long coinId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for id: " + walletId));

        Coin coinToRemove = wallet.getCoinList().stream()
                .filter(coin -> coin.getId().equals(coinId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Coin not found for id: " + coinId));

        wallet.removeCoin(coinToRemove);
        walletRepository.save(wallet);
    }

    public Wallet addCoin(Long walletId, Long coinId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for id: " + walletId));
        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new EntityNotFoundException("Coin not found for id: " + coinId));

        if (wallet.getCoinList().stream().anyMatch(c -> Objects.equals(c.getId(), coin.getId()))) {
            throw new IllegalArgumentException("Coin is already present in the wallet");
        }

        wallet.addCoin(coin);
        return walletRepository.save(wallet);
    }
}
