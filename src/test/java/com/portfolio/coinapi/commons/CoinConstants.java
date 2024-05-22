package com.portfolio.coinapi.commons;

import com.portfolio.coinapi.model.Coin;

import java.math.BigDecimal;
import java.time.Instant;

public class CoinConstants {

    public static final Coin COIN = new Coin("Test Coin",
            new BigDecimal("1.00"),
            new BigDecimal("100.00"),
            new BigDecimal("1.5"),
            Instant.now());

}
