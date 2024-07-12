package com.portfolio.coinapi.DTO;

import com.portfolio.coinapi.model.Wallet;
import com.portfolio.coinapi.util.DTOUtils;

import java.util.Set;

public class WalletDTOFactory {

    public static WalletReturnDTO toDTO(Wallet wallet) {
        String encryptedId = DTOUtils.encryptId(wallet.getId());
        String encryptedClientId = DTOUtils.encryptId(wallet.getClient().getId());

        Set<CoinReturnDTO> coinDTOs = DTOUtils.toCoinDTOs(wallet.getCoinList());

        return new WalletReturnDTO(
                encryptedId,
                encryptedClientId,
                coinDTOs
        );
    }
}
