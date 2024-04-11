package com.portfolio.coinapi.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity(name = "TB_COIN")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(CoinListener.class)
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String name;
    @NotNull
    private BigDecimal price;
    @NotNull
    private BigDecimal marketValue;
    @NotNull
    private BigDecimal last24hChange;
    @NotNull
    private Instant lastUpdate;
    @ManyToOne
    private Wallet wallet;


    public Coin(String name, BigDecimal price, BigDecimal marketValue, BigDecimal last24hChange, Instant lastUpdate ) {
        this.name = name;
        this.price = price;
        this.marketValue = marketValue;
        this.last24hChange = last24hChange;
        this.lastUpdate = lastUpdate;
    }
}
