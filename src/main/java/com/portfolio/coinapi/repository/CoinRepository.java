package com.portfolio.coinapi.repository;

import com.portfolio.coinapi.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin,Long> {

    Optional<Coin> findByName(String name);
}
