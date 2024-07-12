package com.portfolio.coinapi.utils;

import com.portfolio.coinapi.DTO.CoinReturnDTO;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.util.DTOUtils;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.portfolio.coinapi.commons.CoinConstants.COIN;
import static org.junit.jupiter.api.Assertions.*;

class DTOUtilsTest {

    @Test
    void encryptDecryptId_Success() {
        Long originalId = 123L;
        String encryptedId = DTOUtils.encryptId(originalId);
        Long decryptedId = DTOUtils.decryptId(encryptedId);

        assertNotNull(encryptedId);
        assertNotEquals(originalId.toString(), encryptedId);
        assertEquals(originalId, decryptedId);
    }

    @Test
    void toCoinDTOs_Success() {
        Set<Coin> coins = new HashSet<>();

        Coin firstCoin = new Coin();
        firstCoin.setId(1L);
        firstCoin.setName(COIN.getName());
        firstCoin.setPrice(COIN.getPrice());
        firstCoin.setMarketValue(COIN.getMarketValue());
        firstCoin.setLast24hChange(COIN.getLast24hChange());
        firstCoin.setLastUpdate(COIN.getLastUpdate());
        coins.add(firstCoin);

        Coin secondCoin = new Coin();
        secondCoin.setId(2L);
        secondCoin.setName(COIN.getName());
        secondCoin.setPrice(COIN.getPrice());
        secondCoin.setMarketValue(COIN.getMarketValue());
        secondCoin.setLast24hChange(COIN.getLast24hChange());
        secondCoin.setLastUpdate(COIN.getLastUpdate());
        coins.add(secondCoin);

        Set<CoinReturnDTO> coinDTOs = DTOUtils.toCoinDTOs(coins);

        assertNotNull(coinDTOs);
        assertEquals(2, coinDTOs.size());
    }
}
