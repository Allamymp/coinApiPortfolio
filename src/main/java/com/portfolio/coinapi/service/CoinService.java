package com.portfolio.coinapi.service;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.repository.CoinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CoinService {

    private final CoinRepository coinRepository;

    public CoinService(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    public void createCoin(Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("Coin object cannot be null");
        }
        coinRepository.save(coin);
    }

    public void updateCoin(Coin toSaveCoin) {
        if (toSaveCoin == null) {
            throw new IllegalArgumentException("Coin object cannot be null");
        }
        Coin savedCoin = coinRepository.findById(toSaveCoin.getId())
                .orElseThrow(() -> new EntityNotFoundException("Coin not found for id: " + toSaveCoin.getId()));

        if (savedCoin.getCoin_value().compareTo(toSaveCoin.getCoin_value()) != 0) {
            savedCoin.setCoin_value(toSaveCoin.getCoin_value());
        }
        coinRepository.save(savedCoin);
    }
}
