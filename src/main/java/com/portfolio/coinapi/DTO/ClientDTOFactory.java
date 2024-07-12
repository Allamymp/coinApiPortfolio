package com.portfolio.coinapi.DTO;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.util.DTOUtils;

import java.util.Set;

public class ClientDTOFactory {

    public static ClientReturnDTO toDTO(Client client) {
        String encryptedId = DTOUtils.encryptId(client.getId());

        WalletReturnDTO walletDTO = null;
        if (client.getWallet() != null) {
            Set<CoinReturnDTO> coinDTOs = DTOUtils.toCoinDTOs(client.getWallet().getCoinList());

            String encryptedWalletId = DTOUtils.encryptId(client.getWallet().getId());

            walletDTO = new WalletReturnDTO(
                    encryptedWalletId,
                    encryptedId,
                    coinDTOs
            );
        }

        return new ClientReturnDTO(
                encryptedId,
                client.getEmail(),
                client.getPassword(),
                walletDTO
        );
    }
}
