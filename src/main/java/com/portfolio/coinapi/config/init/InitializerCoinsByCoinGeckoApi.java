package com.portfolio.coinapi.config.init;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.service.CoinGeckoService;
import com.portfolio.coinapi.service.CoinService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitializerCoinsByCoinGeckoApi {

    private final CoinGeckoService coinGeckoService;
    private final CoinService coinService;
    private static final Logger logger = LogManager.getLogger(InitializerCoinsByCoinGeckoApi.class);

    public InitializerCoinsByCoinGeckoApi(CoinGeckoService coinGeckoService, CoinService coinService) {
        this.coinGeckoService = coinGeckoService;
        this.coinService = coinService;
    }

    @PostConstruct
    public void controlCoinsData() {
        try {
            logger.info("Initializing coin data...");
            if (coinService.listAll().isEmpty()) {
                List<Coin> coinList = coinGeckoService.fetchCoinDetails();
                coinList.forEach(coinService::createCoin);
                logger.info("Coin data initialized successfully.");
            } else {
                updateCoins();
            }
        } catch (Exception e) {
            logger.error("Error initializing coin data.");
        }
    }

    @Scheduled(fixedDelay = 553000) // 553 seconds
    @CacheEvict(value = "coins", allEntries = true)
    public void updateCoins() {
        try {
            logger.info("Updating coin data...");
            List<Coin> coinList = coinGeckoService.fetchCoinDetails();
            coinList.forEach(coinService::updateCoin);
            logger.info("Coin data updated successfully.");
        } catch (Exception e){
            logger.error("Error updating coin data.",e);
        }
    }


}
