package com.portfolio.coinapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.coinapi.client.CoinGeckoClient;
import com.portfolio.coinapi.config.log.RedisLogger;
import com.portfolio.coinapi.model.enums.CoinsID;
import com.portfolio.coinapi.model.Coin;
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

    private static final String COINS_CACHE_NAME = "coins";
    private static final String FETCH_COINS_DETAILS_ERROR = "Error fetching coin details from CoinGecko";

    private final CoinGeckoClient coinGeckoClient;
    private final ObjectMapper objectMapper;
    private final RedisLogger logger;

    public CoinGeckoService(CoinGeckoClient coinGeckoClient, ObjectMapper objectMapper, RedisLogger logger) {
        this.coinGeckoClient = coinGeckoClient;
        this.objectMapper = objectMapper;
        this.logger = logger;
    }

    public Boolean checkPingStatus() {
        logger.log("info", "Checking CoinGecko ping status.");
        ResponseEntity<?> responseEntity = coinGeckoClient.ping();
        boolean isOk = responseEntity.getStatusCode() == HttpStatus.OK;
        logger.log("info", "Ping status received: " + (isOk ? "OK" : "NOT OK"));
        return isOk;
    }

    @Cacheable(COINS_CACHE_NAME)
    public List<Coin> fetchCoinDetails() {
        logger.log("info", "Fetching coin details from CoinGecko.");
        ResponseEntity<String> response = coinGeckoClient
                .getCoinsDetailsRaw(getAllCoinIdsAsString(), "usd"
                        , true
                        , true
                        , true);

        String responseBody = response.getBody();
        if (responseBody == null) {
            String errorMessage = "Empty response body received from CoinGecko.";
            logger.log("warn", errorMessage);
            throw new RuntimeException(errorMessage);
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            String errorMessage = "Error processing JSON from CoinGecko: " + responseBody;
            logger.log("warn", errorMessage);
            throw new RuntimeException(FETCH_COINS_DETAILS_ERROR, e);
        }

        if (root == null) {
            String errorMessage = "Root JSON node is null.";
            logger.log("warn", errorMessage);
            throw new RuntimeException(errorMessage);
        }

        List<Coin> listCoins = new ArrayList<>();
        Iterator<String> names = root.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            JsonNode details = root.get(name);
            try {
                Coin coin = new Coin(
                        name,
                        new BigDecimal(details.get("usd").asText()),
                        new BigDecimal(details.get("usd_market_cap").asText()),
                        new BigDecimal(details.get("usd_24h_change").asText()),
                        Instant.ofEpochSecond(details.get("last_updated_at").asLong())
                );
                listCoins.add(coin);
            } catch (Exception e) {
                logger.log("warn", "Error parsing coin details for " + name + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        logger.log("info", "Received and processed coin details for " + listCoins.size() + " coins.");
        return listCoins;
    }

    public String getAllCoinIdsAsString() {
        logger.log("info", "Getting all coin IDs as a comma-separated string.");
        return Arrays.stream(CoinsID.values())
                .map(CoinsID::getId)
                .collect(Collectors.joining(","));
    }
}
