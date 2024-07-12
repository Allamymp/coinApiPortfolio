package com.portfolio.coinapi.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
@Entity(name = "TB_COIN")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Coin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToMany(mappedBy = "coinList")
    @JsonIgnore
    private Set<Wallet> wallets = new HashSet<>();

    public Coin(String name, BigDecimal price, BigDecimal marketValue, BigDecimal last24hChange, Instant lastUpdate ) {
        this.name = name;
        this.price = price;
        this.marketValue = marketValue;
        this.last24hChange = last24hChange;
        this.lastUpdate = lastUpdate;
    }
}
