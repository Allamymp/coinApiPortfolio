package com.portfolio.coinapi.service;

import com.portfolio.coinapi.DTO.CoinDTOFactory;
import com.portfolio.coinapi.DTO.CoinReturnDTO;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.repository.CoinRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoinService {

    private static final String COIN_NULL_ERROR = "Coin object cannot be null";
    private static final String COIN_NOT_FOUND_ERROR = "Coin not found for name: ";

    private final CoinRepository coinRepository;
    private final RedisLogger logger;

    public CoinService(CoinRepository coinRepository, RedisLogger logger) {
        this.coinRepository = coinRepository;
        this.logger = logger;
    }

    @Transactional
    public void createCoin(Coin coin) {
        validateCoin(coin);
        logger.log("info", "Creating coin with name: " + coin.getName());
        coinRepository.save(coin);
        logger.log("info", "Coin successfully created: " + coin.getName());
    }

    @Transactional
    public void updateCoin(Coin toSaveCoin) {
        validateCoin(toSaveCoin);
        Coin savedCoin = coinRepository.findByName(toSaveCoin.getName())
                .orElseThrow(() -> {
                    logger.log("warn", COIN_NOT_FOUND_ERROR + toSaveCoin.getName());
                    return new EntityNotFoundException(COIN_NOT_FOUND_ERROR + toSaveCoin.getName());
                });

        logger.log("info", "Updating coin: " + toSaveCoin.getName());
        boolean updateRequired = updateCoinDetails(savedCoin, toSaveCoin);

        if (updateRequired) {
            coinRepository.save(savedCoin);
            logger.log("info", "Coin updated successfully: " + savedCoin.getName());
        } else {
            logger.log("info", "No updates required for coin: " + savedCoin.getName());
        }
    }

    @Transactional(readOnly = true)
    public Page<CoinReturnDTO> listAll() {
        logger.log("info", "Listing all coins");
        List<Coin> coinList = coinRepository.findAll();
        List<CoinReturnDTO> coinDTOList = coinList.stream()
                .map(CoinDTOFactory::toDTO)
                .collect(Collectors.toList());

        logger.log("info", "Total coins listed: " + coinDTOList.size());

        Pageable pageable = PageRequest.of(0, coinDTOList.size());
        return new PageImpl<>(coinDTOList, pageable, coinDTOList.size());
    }
    @Transactional(readOnly = true)
    public ResponseEntity<CoinReturnDTO> findByName(String name) {
        logger.log("info", "Searching coin by name: " + name);
        Optional<Coin> coin = coinRepository.findByName(name);
        return coin.map(value -> {
            logger.log("info", "Coin found for name " + name);
            return ResponseEntity.ok(CoinDTOFactory.toDTO(value));
        }).orElseGet(() -> {
            logger.log("info", "Coin not found for name: " + name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }

    private void validateCoin(Coin coin) {
        if (coin == null) {
            logger.log("warn", COIN_NULL_ERROR);
            throw new IllegalArgumentException(COIN_NULL_ERROR);
        }
    }

    private <T> boolean isDifferent(Comparable<T> obj1, T obj2) {
        return obj1 == null || obj2 == null || obj1.compareTo(obj2) != 0;
    }

    private boolean updateCoinDetails(Coin savedCoin, Coin toSaveCoin) {
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

        return updateRequired;
    }
}
