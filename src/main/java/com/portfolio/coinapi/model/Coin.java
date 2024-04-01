package com.portfolio.coinapi.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

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
    private BigDecimal coin_value;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @ManyToOne
    private Wallet wallet;


    public Coin(String name, BigDecimal coin_value) {
        this.name = name;
        this.coin_value = coin_value;
    }
}
