package com.portfolio.coinapi.config.init;

import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.service.CoinGeckoService;
import com.portfolio.coinapi.service.CoinService;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitializerCoinsByCoinGeckoApi {

    private final CoinGeckoService coinGeckoService;
    private final CoinService coinService;
    private final RedisLogger logger;

    public InitializerCoinsByCoinGeckoApi(CoinGeckoService coinGeckoService, CoinService coinService, RedisLogger logger) {
        this.coinGeckoService = coinGeckoService;
        this.coinService = coinService;
        this.logger = logger;
    }

    @PostConstruct
    public void controlCoinsData() {
        try {
            logger.log("info", "Initializing coin data...");
            if (coinService.listAll().isEmpty()) {
                List<Coin> coinList = coinGeckoService.fetchCoinDetails();
                coinList.forEach(coinService::createCoin);
                logger.log("info", "Coin data initialized successfully.");
            } else {
                updateCoins();
            }
        } catch (Exception e) {
            logger.log("warn", "Error initializing coin data.");
        }
    }

    @Scheduled(fixedDelay = 553000) // 553 seconds
    @CacheEvict(value = "coins", allEntries = true)
    public void updateCoins() {
        try {
            logger.log("info", "Updating coin data...");
            List<Coin> coinList = coinGeckoService.fetchCoinDetails();
            coinList.forEach(coinService::updateCoin);
            logger.log("info", "Coin data updated successfully.");
        } catch (Exception e) {
            logger.log("warn", "Error updating coin data.");
        }
    }


}
