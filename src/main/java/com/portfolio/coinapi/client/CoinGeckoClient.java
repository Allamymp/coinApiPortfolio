package com.portfolio.coinapi.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="coinGeckoClient", url="https://api.coingecko.com/api/v3")
public class CoinGeckoClient {
}
