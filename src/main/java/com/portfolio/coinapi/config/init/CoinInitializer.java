package com.portfolio.coinapi.config.init;

import com.portfolio.coinapi.client.CoinGeckoClient;
import org.springframework.stereotype.Component;

@Component
public class CoinInitializer {

    private final CoinGeckoClient coinGeckoClient;

    public CoinInitializer(CoinGeckoClient coinGeckoClient) {
        this.coinGeckoClient = coinGeckoClient;
    }
}
