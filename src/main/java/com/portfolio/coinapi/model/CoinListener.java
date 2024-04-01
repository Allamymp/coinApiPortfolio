package com.portfolio.coinapi.model;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CoinListener {

    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate(Coin coin){
        coin.setLastUpdate(new Date());
    }
}
