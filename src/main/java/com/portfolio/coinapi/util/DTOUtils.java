package com.portfolio.coinapi.util;

import com.portfolio.coinapi.DTO.CoinReturnDTO;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.util.EncryptUtil;

import java.util.Set;
import java.util.stream.Collectors;

public class DTOUtils {

    public static String encryptId(Long id) {
        try {
            return EncryptUtil.encrypt(id.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting ID", e);
        }
    }

    public static Long decryptId(String encryptedId) {
        try {
            return Long.parseLong(EncryptUtil.decrypt(encryptedId));
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting ID", e);
        }
    }

    public static Set<CoinReturnDTO> toCoinDTOs(Set<Coin> coins) {
        return coins.stream()
                .map(coin -> {
                    String encryptedCoinId = encryptId(coin.getId());
                    return new CoinReturnDTO(
                            encryptedCoinId,
                            coin.getName(),
                            coin.getPrice(),
                            coin.getMarketValue(),
                            coin.getLast24hChange(),
                            coin.getLastUpdate()
                    );
                })
                .collect(Collectors.toSet());
    }
}
