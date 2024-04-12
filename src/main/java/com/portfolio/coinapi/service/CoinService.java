package com.portfolio.coinapi.service;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.repository.CoinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Coin savedCoin = coinRepository.findByName(toSaveCoin.getName())
                .orElseThrow(() -> new EntityNotFoundException("Coin not found for name: " + toSaveCoin.getName()));

        boolean updateRequired = false;

        if (savedCoin.getPrice() == null || savedCoin.getPrice().compareTo(toSaveCoin.getPrice()) != 0) {
            savedCoin.setPrice(toSaveCoin.getPrice());
            updateRequired = true;
        }
        if (savedCoin.getLastUpdate() == null || savedCoin.getLastUpdate().isBefore(toSaveCoin.getLastUpdate())) {
            savedCoin.setLastUpdate(toSaveCoin.getLastUpdate());
            updateRequired = true;
        }
        if (savedCoin.getLast24hChange() == null || savedCoin.getLast24hChange().compareTo(toSaveCoin.getLast24hChange()) != 0) {
            savedCoin.setLast24hChange(toSaveCoin.getLast24hChange());
            updateRequired = true;
        }
        if (savedCoin.getMarketValue() == null || savedCoin.getMarketValue().compareTo(toSaveCoin.getMarketValue()) != 0) {
            savedCoin.setMarketValue(toSaveCoin.getMarketValue());
            updateRequired = true;
        }

        if (updateRequired) {
            coinRepository.save(savedCoin);
        }
    }
    public List<Coin> listAll(){
        return coinRepository.findAll();
    }
}
