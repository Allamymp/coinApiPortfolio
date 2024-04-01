package com.portfolio.coinapi.repository;

import com.portfolio.coinapi.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin,Long> {
}
