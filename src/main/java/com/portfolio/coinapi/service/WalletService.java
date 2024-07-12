package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.repository.CoinRepository;
import com.portfolio.coinapi.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WalletService {

    private static final String WALLET_NULL_ERROR = "Wallet object cannot be null";
    private static final String WALLET_NOT_FOUND_ERROR = "Wallet not found for id: ";
    private static final String COIN_NOT_FOUND_ERROR = "Coin not found for id: ";
    private static final String DUPLICATE_COIN_ERROR = "Coin is already present in the wallet";

    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;
    private final RedisLogger logger;

    public WalletService(WalletRepository walletRepository, CoinRepository coinRepository, RedisLogger logger) {
        this.walletRepository = walletRepository;
        this.coinRepository = coinRepository;
        this.logger = logger;
    }

    @Transactional
    public Wallet create(Wallet wallet) {
        validateWallet(wallet);
        logger.log("info", "Creating wallet with ID: " + wallet.getId());
        Wallet createdWallet = walletRepository.save(wallet);
        logger.log("info", "Wallet created successfully with ID: " + createdWallet.getId());
        return createdWallet;
    }

    @Transactional(readOnly = true)
    public Set<Coin> listCoinsByWalletId(Long walletId) {
        logger.log("info", "Retrieving coins for wallet ID: " + walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.log("warn", WALLET_NOT_FOUND_ERROR + walletId);
                    return new EntityNotFoundException(WALLET_NOT_FOUND_ERROR + walletId);
                });
        Set<Coin> coins = new HashSet<>(wallet.getCoinList());
        logger.log("info", "Number of coins retrieved for wallet ID " + walletId + ": " + coins.size());
        return coins;
    }

    @Transactional
    public void removeCoin(Long walletId, Long coinId) {
        logger.log("info", "Attempting to remove coin ID: " + coinId + " from wallet ID: " + walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.log("warn", WALLET_NOT_FOUND_ERROR + walletId);
                    return new EntityNotFoundException(WALLET_NOT_FOUND_ERROR + walletId);
                });
        Coin coinToRemove = wallet.getCoinList().stream()
                .filter(coin -> coin.getId().equals(coinId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.log("warn", COIN_NOT_FOUND_ERROR + coinId + " in wallet ID: " + walletId);
                    return new EntityNotFoundException(COIN_NOT_FOUND_ERROR + coinId);
                });
        wallet.removeCoin(coinToRemove);
        walletRepository.save(wallet);
        logger.log("info", "Removed coin ID: " + coinId + " from wallet ID: " + walletId);
    }

    @Transactional
    public Wallet addCoin(Long walletId, Long coinId) {
        logger.log("info", "Adding coin ID: " + coinId + " to wallet ID: " + walletId);
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.log("warn", WALLET_NOT_FOUND_ERROR + walletId);
                    return new EntityNotFoundException(WALLET_NOT_FOUND_ERROR + walletId);
                });
        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> {
                    logger.log("warn", COIN_NOT_FOUND_ERROR + coinId);
                    return new EntityNotFoundException(COIN_NOT_FOUND_ERROR + coinId);
                });

        if (wallet.getCoinList().stream().anyMatch(c -> Objects.equals(c.getId(), coin.getId()))) {
            logger.log("warn", "Attempted to add duplicate coin ID: " + coinId + " to wallet ID: " + walletId);
            throw new IllegalArgumentException(DUPLICATE_COIN_ERROR);
        }

        wallet.addCoin(coin);
        Wallet savedWallet = walletRepository.save(wallet);
        logger.log("info", "Added coin ID: " + coinId + " to wallet ID: " + walletId);
        return savedWallet;
    }

    private void validateWallet(Wallet wallet) {
        if (wallet == null) {
            logger.log("warn", WALLET_NULL_ERROR);
            throw new IllegalArgumentException(WALLET_NULL_ERROR);
        }
    }
}
