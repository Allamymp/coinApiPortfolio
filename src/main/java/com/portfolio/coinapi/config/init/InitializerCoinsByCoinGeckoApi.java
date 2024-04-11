package com.portfolio.coinapi.config.init;

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

    public InitializerCoinsByCoinGeckoApi(CoinGeckoService coinGeckoService, CoinService coinService) {
        this.coinGeckoService = coinGeckoService;
        this.coinService = coinService;
    }

    @PostConstruct
    public void controlCoinsData() {
        try {
            if (coinService.listAll().isEmpty()) {
                List<Coin> coinList = coinGeckoService.fetchCoinDetails();
                coinList.forEach(coinService::createCoin);
            } else {
                updateCoins();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it accordingly.
        }
    }

    @Scheduled(fixedDelay = 553000) // 553 segundos
    @CacheEvict(value = "coins", allEntries = true)
    public void updateCoins() throws InterruptedException {
        List<Coin> coinList = coinGeckoService.fetchCoinDetails();
        coinList.forEach(coinService::updateCoin);
    }


}
