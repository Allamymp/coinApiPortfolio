package com.portfolio.coinapi.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coinGeckoClient", url = "${coin.gecko.api.url}")
public interface CoinGeckoClient {

    @GetMapping(value = "/ping", headers = {"accept=application/json", "x-cg-demo-api-key=${coin.gecko.api.key}"})
    ResponseEntity<?> ping();

    @GetMapping(value = "/simple/price", headers = {"accept=application/json", "x-cg-demo-api-key=${coin.gecko.api.key}"})
    ResponseEntity<String> getCoinsDetailsRaw(
            @RequestParam("ids") String ids,
            @RequestParam("vs_currencies") String vsCurrencies,
            @RequestParam("include_market_cap") boolean includeMarketCap,
            @RequestParam("include_24hr_change") boolean include24hrChange,
            @RequestParam("include_last_updated_at") boolean includeLastUpdatedAt);


}

