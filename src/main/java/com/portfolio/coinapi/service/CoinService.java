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

        if (savedCoin.getPrice().compareTo(toSaveCoin.getPrice()) != 0) {
            savedCoin.setPrice(toSaveCoin.getPrice());
        }
        coinRepository.save(savedCoin);
    }
    public List<Coin> listAll(){
        return coinRepository.findAll();
    }
}
