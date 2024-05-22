package com.portfolio.coinapi.commons;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class ClientConstants {

    public static final Client CLIENT = new Client(
            "clientName",
            "clientPassword",
            new Wallet());
    public static final Client INVALID_CLIENT = new Client(null, null, null);
    public static final Client EMPTY_CLIENT = new Client("", "", new Wallet());
    public static final List<Client> CLIENT_LIST = new ArrayList<>() {
        {
            add(new Client("firstClient", "firstClientPassword", new Wallet()));
            add(new Client("secondClient", "secondClientPassword", new Wallet()));
            add(new Client("thirdClient", "thirdClientPassword", new Wallet()));
        }
    };

}
