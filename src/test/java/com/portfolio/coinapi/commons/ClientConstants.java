package com.portfolio.coinapi.commons;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientConstants {

    public static final Client CLIENT;

    static {
        CLIENT = new Client("clientName@email.com", "Valid1Pass!", new Wallet());
        CLIENT.setUniqueToken(UUID.randomUUID().toString());
        CLIENT.getWallet().setClient(CLIENT);
    }

    public static final Client INVALID_CLIENT = new Client(null, null, null);

    public static final Client EMPTY_CLIENT;

    static {
        EMPTY_CLIENT = new Client("", "", new Wallet());
        EMPTY_CLIENT.setUniqueToken(UUID.randomUUID().toString());
        EMPTY_CLIENT.getWallet().setClient(EMPTY_CLIENT); // Ensure wallet is correctly set up
    }

    public static final List<Client> CLIENT_LIST = new ArrayList<>() {
        {
            Client firstClient = new Client("firstClient@email.com", "1Password", new Wallet());
            firstClient.setUniqueToken(UUID.randomUUID().toString());
            firstClient.getWallet().setClient(firstClient);
            add(firstClient);

            Client secondClient = new Client("secondClient@email.com", "2Password", new Wallet());
            secondClient.setUniqueToken(UUID.randomUUID().toString());
            secondClient.getWallet().setClient(secondClient);
            add(secondClient);

            Client thirdClient = new Client("thirdClient@email.com", "3Password", new Wallet());
            thirdClient.setUniqueToken(UUID.randomUUID().toString());
            thirdClient.getWallet().setClient(thirdClient);
            add(thirdClient);
        }
    };
}
