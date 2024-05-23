package com.portfolio.coinapi.repository;


import com.portfolio.coinapi.model.Coin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static com.portfolio.coinapi.commons.CoinConstants.COIN;
import static com.portfolio.coinapi.commons.CoinConstants.INVALID_COIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class CoinRepositoryTest {

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        COIN.setId(null);
    }

    @Test
    public void createCoin_withValidData_returnsCoin() {

        Coin coin = coinRepository.save(COIN);
        Coin sut = testEntityManager.find(Coin.class, coin.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(COIN.getName());
        assertThat(sut.getId()).isEqualTo(coin.getId());
    }

    @Test
    public void createCoin_withInvalidData_throwsException() {

        assertThatThrownBy(() -> coinRepository.save(INVALID_COIN)).isInstanceOf(Exception.class);
    }

    @Test
    public void getCoin_byValidName_returnsCoin() {

        Coin coin = coinRepository.save(COIN);
        Optional<Coin> sut = coinRepository.findByName(coin.getName());

        assertThat(sut).isPresent();
        assertThat(sut.get().getName()).isEqualTo(coin.getName());
        assertThat(sut.get().getPrice()).isEqualTo(coin.getPrice());
    }

    @Test
    public void getCoin_byUnexistingName_returnsEmpty() {
        Optional<Coin> sut = coinRepository.findByName("invalid_name");
        assertThat(sut).isEmpty();
    }

    @Test
    public void getCoin_byExistingId_returnsCoin() {
        Coin coin = coinRepository.save(COIN);
        Optional<Coin> sut = coinRepository.findById(coin.getId());

        assertThat(sut).isNotEmpty();
        assertThat(sut.get().getId()).isEqualTo(coin.getId());
    }

    @Test
    public void getCoin_byUnexistingId_returnsEmpty() {
        Optional<Coin> sut = coinRepository.findById(1L);
        assertThat(sut).isEmpty();
    }
}
