package com.portfolio.coinapi.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "TB_WALLET")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @JsonBackReference
    private Client client;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "wallet_coin",
            joinColumns = @JoinColumn(name = "wallet_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "coin_id", referencedColumnName = "id")
    )
    private Set<Coin> coinList = new HashSet<>();

    public Wallet(Client client) {
        this.client = client;
    }

    public void addCoin(Coin coin) {
        this.coinList.add(coin);
        coin.getWallets().add(this);
    }

    public void removeCoin(Coin coin) {
        coinList.remove(coin);
    }
}
