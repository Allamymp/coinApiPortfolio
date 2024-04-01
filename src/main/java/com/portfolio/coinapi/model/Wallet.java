package com.portfolio.coinapi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name="TB_WALLET")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<Coin> coinList = new ArrayList<>();

    public void addCoin(Coin coin) {
        coin.setWallet(this);
        coinList.add(coin);
    }

    public void removeCoin(Coin coin){
        coinList.remove(coin);
    }
}
