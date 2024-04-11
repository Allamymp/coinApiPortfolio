package com.portfolio.coinapi.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coinGeckoClient", url = "${coin.gecko.api.url}")
public interface CoinGeckoClient {

    @GetMapping(value = "/ping", headers = {"accept=application/json", "x-cg-demo-api-key=${coin.gecko.api.key}"})
    ResponseEntity<?> ping();

    @GetMapping(value = "/simple/price"
            , headers = {"accept=application/json", "x-cg-demo-api-key=${coin.gecko.api.key}"}
            , params = {"vs_currencies=usd",
            "includes_market_cap=true",
            "include_24hr_change=true",
            "include_last_updated_at=true"})
   ResponseEntity<String> getCoinsDetailsRaw(@RequestParam("ids") String ids);

}

