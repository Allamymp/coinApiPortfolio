package com.portfolio.coinapi.config.enums;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum CoinsID {
    BITCOIN("bitcoin"),
    BNB("binancecoin"),
    ETHEREUM("ethereum"),
    SOLANA("solana"),
    TETHER("tether"),
    XRP("ripple"),
    USD_COIN("usd-coin"),
    DOGECOIN("dogecoin"),
    TONCOIN("the-open-network"),
    CARDANO("cardano");

    private final String id;

    CoinsID(String id){
        this.id = id;
    }
}