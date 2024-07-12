package com.portfolio.coinapi.DTO;

import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.util.EncryptUtil;

public class CoinDTOFactory {
    public static CoinReturnDTO toDTO(Coin coin) {
        String encryptedId;
        try {
            encryptedId = EncryptUtil.encrypt(coin.getId().toString());
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting ID", e);
        }

        return new CoinReturnDTO(
                encryptedId,
                coin.getName(),
                coin.getPrice(),
                coin.getMarketValue(),
                coin.getLast24hChange(),
                coin.getLastUpdate()
        );
    }
}
