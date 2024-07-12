package com.portfolio.coinapi.DTO;

import java.math.BigDecimal;
import java.time.Instant;

public record CoinReturnDTO(String id,
                            String name,
                            BigDecimal usd_price,
                            BigDecimal usd_market_value,
                            BigDecimal last_24h_change,
                            Instant last_update) {
}
