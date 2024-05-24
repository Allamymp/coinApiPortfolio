package com.portfolio.coinapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.coinapi.client.CoinGeckoClient;
import com.portfolio.coinapi.model.enums.CoinsID;
import com.portfolio.coinapi.model.Coin;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoinGeckoService {

    private final CoinGeckoClient coinGeckoClient;
    private final ObjectMapper objectMapper;
    private final Logger coinGeckoServiceLogger;

    public CoinGeckoService(CoinGeckoClient coinGeckoClient, ObjectMapper objectMapper, Logger coinGeckoServiceLogger) {
        this.coinGeckoClient = coinGeckoClient;
        this.objectMapper = objectMapper;
        this.coinGeckoServiceLogger = coinGeckoServiceLogger;
    }

    public Boolean checkPingStatus() {
        coinGeckoServiceLogger.info("Checking CoinGecko ping status.");
        ResponseEntity<?> responseEntity = coinGeckoClient.ping();
        boolean isOk = responseEntity.getStatusCode() == HttpStatus.OK;
        coinGeckoServiceLogger.info("Ping status received: " + (isOk ? "OK" : "NOT OK"));
        return isOk;
    }

    @Cacheable("coins")
    public List<Coin> fetchCoinDetails() {
        coinGeckoServiceLogger.info("Fetching coin details from CoinGecko.");
        ResponseEntity<String> response = coinGeckoClient
                .getCoinsDetailsRaw(
                        getAllCoinIdsAsString()
                        , "usd"
                        , true
                        , true
                        , true);
        JsonNode root;
        try {
            String responseBody = response.getBody();
            if (responseBody == null) {
                coinGeckoServiceLogger.error("Empty response body received from CoinGecko.");
                throw new RuntimeException("Empty response body received from CoinGecko.");
            }
            root = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            coinGeckoServiceLogger.error("Error processing JSON from CoinGecko: " + response.getBody(), e);
            throw new RuntimeException(e);
        }

        if (root == null) {
            coinGeckoServiceLogger.error("Root JSON node is null.");
            throw new RuntimeException("Root JSON node is null.");
        }

        List<Coin> listCoins = new ArrayList<>();
        Iterator<String> names = root.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            JsonNode details = root.get(name);
            Coin coin = new Coin(
                    name,
                    new BigDecimal(details.get("usd").asText()),
                    new BigDecimal(details.get("usd_market_cap").asText()),
                    new BigDecimal(details.get("usd_24h_change").asText()),
                    Instant.ofEpochSecond(details.get("last_updated_at").asLong())
            );
            listCoins.add(coin);
        }
        coinGeckoServiceLogger.info("Received and processed coin details for " + listCoins.size() + " coins.");
        return listCoins;
    }

    public String getAllCoinIdsAsString() {
        coinGeckoServiceLogger.info("CoinGeckoService: calling  getAllCoinIdsAsString.");

        return Arrays.stream(CoinsID.values())
                .map(CoinsID::getId)
                .collect(Collectors.joining(","));
    }
}
