package com.portfolio.coinapi.repository;

import com.portfolio.coinapi.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoinRepository extends JpaRepository<Coin,Long> {

    Optional<Coin> findByName(String name);
}
