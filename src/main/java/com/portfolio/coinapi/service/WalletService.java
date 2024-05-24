package com.portfolio.coinapi.service;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.CoinRepository;
import com.portfolio.coinapi.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;
    private final Logger WalletServiceLogger;

    public WalletService(WalletRepository walletRepository,
                         CoinRepository coinRepository, Logger walletServiceLogger) {
        this.walletRepository = walletRepository;
        this.coinRepository = coinRepository;
        WalletServiceLogger = walletServiceLogger;
    }

    public Wallet create(Wallet wallet) {
        if (wallet == null) {
            WalletServiceLogger.error("Failed to create wallet: wallet object is null.");
            throw new IllegalArgumentException("Wallet object cannot be null");
        }
        WalletServiceLogger.info("Created wallet with ID: " + wallet.getId());
        return walletRepository.save(wallet);
    }


    public Set<Coin> listCoinsByWalletId(Long walletId) {
        WalletServiceLogger.info("Retrieving coins for wallet ID: " + walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    WalletServiceLogger.error("Wallet not found for ID: " + walletId);
                    return new EntityNotFoundException("Wallet not found for id: " + walletId);
                });
        Set<Coin> coins = new HashSet<>(wallet.getCoinList());
        WalletServiceLogger.info("Number of coins retrieved for wallet ID " + walletId + ": " + coins.size());
        return coins;
    }

    public void removeCoin(Long walletId, Long coinId) {
        WalletServiceLogger.info("Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> {
            WalletServiceLogger.error("Wallet not found for ID: " + walletId);
            return new EntityNotFoundException("Wallet not found for id: " + walletId);
        });
        Coin coinToRemove = wallet.getCoinList().stream()
                .filter(coin -> coin.getId().equals(coinId))
                .findFirst()
                .orElseThrow(() -> {
                    WalletServiceLogger.error("Coin not found for ID: " + coinId + " in wallet ID: " + walletId);
                    return new EntityNotFoundException("Coin not found for id: " + coinId);
                });
        wallet.removeCoin(coinToRemove);
        walletRepository.save(wallet);
        WalletServiceLogger.info("Removed coin ID: " + coinId + " from wallet ID: " + walletId);
    }

    public Wallet addCoin(Long walletId, Long coinId) {
        WalletServiceLogger.info("Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    WalletServiceLogger.error("Wallet not found for ID: " + walletId);
                    return new EntityNotFoundException("Wallet not found for id: " + walletId);
                });
        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> {
                    WalletServiceLogger.error("Coin not found for ID: " + coinId);
                    return new EntityNotFoundException("Coin not found for id: " + coinId);
                });

        if (wallet.getCoinList().stream().anyMatch(c -> Objects.equals(c.getId(), coin.getId()))) {
            WalletServiceLogger.warn("Attempted to add duplicate coin ID: " + coinId + " to wallet ID: " + walletId);
            throw new IllegalArgumentException("Coin is already present in the wallet");
        }

        wallet.addCoin(coin);
        Wallet savedWallet = walletRepository.save(wallet);
        WalletServiceLogger.info("Added coin ID: " + coinId + " to wallet ID: " + walletId);
        return savedWallet;
    }


}
