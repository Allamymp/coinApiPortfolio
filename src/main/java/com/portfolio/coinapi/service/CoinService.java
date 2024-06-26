package com.portfolio.coinapi.service;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.repository.CoinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CoinService {

    private static final String COIN_NULL_ERROR = "Coin object cannot be null";
    private static final String COIN_NOT_FOUND_ERROR = "Coin not found for name: ";

    private final CoinRepository coinRepository;
    private final RedisLogger logger;

    public CoinService(CoinRepository coinRepository, RedisLogger logger) {
        this.coinRepository = coinRepository;
        this.logger = logger;
    }


    public void createCoin(Coin coin) {
        validateCoin(coin);
        logger.log("info", "Creating coin with name: " + coin.getName());
        coinRepository.save(coin);
        logger.log("info", "Coin successfully created: " + coin.getName());
    }


    public void updateCoin(Coin toSaveCoin) {
        validateCoin(toSaveCoin);
        Coin savedCoin = coinRepository.findByName(toSaveCoin.getName())
                .orElseThrow(() -> {
                    logger.log("warn", COIN_NOT_FOUND_ERROR + toSaveCoin.getName());
                    return new EntityNotFoundException(COIN_NOT_FOUND_ERROR + toSaveCoin.getName());
                });

        logger.log("info", "Updating coin: " + toSaveCoin.getName());
        boolean updateRequired = false;

        if (isDifferent(savedCoin.getPrice(), toSaveCoin.getPrice())) {
            logger.log("info", "Updating price for coin: " + savedCoin.getName());
            savedCoin.setPrice(toSaveCoin.getPrice());
            updateRequired = true;
        }
        if (isDifferent(savedCoin.getLastUpdate(), toSaveCoin.getLastUpdate())) {
            logger.log("info", "Updating LastUpdate for coin: " + savedCoin.getName());
            savedCoin.setLastUpdate(toSaveCoin.getLastUpdate());
            updateRequired = true;
        }
        if (isDifferent(savedCoin.getLast24hChange(), toSaveCoin.getLast24hChange())) {
            logger.log("info", "Updating last 24h change for coin: " + savedCoin.getName());
            savedCoin.setLast24hChange(toSaveCoin.getLast24hChange());
            updateRequired = true;
        }
        if (isDifferent(savedCoin.getMarketValue(), toSaveCoin.getMarketValue())) {
            logger.log("info", "Updating market value for coin: " + savedCoin.getName());
            savedCoin.setMarketValue(toSaveCoin.getMarketValue());
            updateRequired = true;
        }

        if (updateRequired) {
            coinRepository.save(savedCoin);
            logger.log("info", "Coin updated successfully: " + savedCoin.getName());
        } else {
            logger.log("info", "No updates required for coin: " + savedCoin.getName());
        }
    }


    public List<Coin> listAll() {
        logger.log("info", "Listing all coins");
        List<Coin> coinList = coinRepository.findAll();
        logger.log("info", "Total coins listed: " + coinList.size());
        return coinList;
    }


    private void validateCoin(Coin coin) {
        if (coin == null) {
            logger.log("warn", COIN_NULL_ERROR);
            throw new IllegalArgumentException(COIN_NULL_ERROR);
        }
    }

    /**
     * Checks if two comparable objects are different.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return true if they are different, false otherwise
     */
    private <T> boolean isDifferent(Comparable<T> obj1, T obj2) {
        return obj1 == null || obj2 == null || obj1.compareTo(obj2) != 0;
    }
}
