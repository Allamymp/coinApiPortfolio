package com.portfolio.coinapi.service;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.repository.CoinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CoinService {

    private final CoinRepository coinRepository;
    private static final Logger coinServiceLogger = LogManager.getLogger(CoinService.class);


    public CoinService(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }


    public void createCoin(Coin coin) {
        coinServiceLogger.info("CoinService: verifying if coin is not null");
        if (coin == null) {
            coinServiceLogger.error("Attempted to create a null coin object.");
            throw new IllegalArgumentException("Coin object cannot be null");
        }
        coinServiceLogger.info("Creating coin with name: " + coin.getName());
        coinRepository.save(coin);
        coinServiceLogger.info("Coin successfully created: " + coin.getName());
    }

    public void updateCoin(Coin toSaveCoin) {
        if (toSaveCoin == null) {
            coinServiceLogger.error("Attempted to update a null coin object.");
            throw new IllegalArgumentException("Coin object cannot be null");
        }
        Coin savedCoin = coinRepository.findByName(toSaveCoin.getName())
                .orElseThrow(() -> new EntityNotFoundException("Coin not found for name: " + toSaveCoin.getName()));
        coinServiceLogger.info("Updating coin: " + toSaveCoin.getName());
        boolean updateRequired = false;

        if (savedCoin.getPrice() == null || savedCoin.getPrice().compareTo(toSaveCoin.getPrice()) != 0) {
            coinServiceLogger.debug("Updating price for coin: " + savedCoin.getName());
            savedCoin.setPrice(toSaveCoin.getPrice());
            updateRequired = true;
        }
        if (savedCoin.getLastUpdate() == null || savedCoin.getLastUpdate().isBefore(toSaveCoin.getLastUpdate())) {
            coinServiceLogger.debug("Updating LastUpdate for coin: " + savedCoin.getName());
            savedCoin.setLastUpdate(toSaveCoin.getLastUpdate());
            updateRequired = true;
        }
        if (savedCoin.getLast24hChange() == null || savedCoin.getLast24hChange().compareTo(toSaveCoin.getLast24hChange()) != 0) {
            coinServiceLogger.debug("Updating last 24h change for coin: " + savedCoin.getName());
            savedCoin.setLast24hChange(toSaveCoin.getLast24hChange());
            updateRequired = true;
        }
        if (savedCoin.getMarketValue() == null || savedCoin.getMarketValue().compareTo(toSaveCoin.getMarketValue()) != 0) {
            coinServiceLogger.debug("Updating market value for coin: " + savedCoin.getName());
            savedCoin.setMarketValue(toSaveCoin.getMarketValue());
            updateRequired = true;
        }

        if (updateRequired) {
            coinServiceLogger.info("Coin updated successfully: " + savedCoin.getName());
            coinRepository.save(savedCoin);
        }else{
            coinServiceLogger.info("No updates required for coin: " + savedCoin.getName());
        }
    }

    public List<Coin> listAll() {
        coinServiceLogger.info("Listing all coins");
        List<Coin> coinList = coinRepository.findAll();
        coinServiceLogger.info("Total coins listed: " + coinList.size());
        return coinList;
    }
}
