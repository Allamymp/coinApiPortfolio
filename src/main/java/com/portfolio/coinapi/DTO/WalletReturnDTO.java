package com.portfolio.coinapi.DTO;

import java.util.Set;

public record WalletReturnDTO(String id, String clientId, Set<CoinReturnDTO> coins) {
}
