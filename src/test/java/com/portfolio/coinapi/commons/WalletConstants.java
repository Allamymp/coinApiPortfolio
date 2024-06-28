package com.portfolio.coinapi.commons;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletConstants {

    public static final Wallet INVALID_WALLET = new Wallet();

    public static final Wallet WALLET = new Wallet( new Client("clientName", "clientPassword"));

    public static final List<Wallet> WALLET_LIST = new ArrayList<>() {{
        add(new Wallet(new Client("client1", "password1")));
        add(new Wallet(new Client("client2", "password2")));
        add(new Wallet(new Client("client3", "password3")));
    }};
}