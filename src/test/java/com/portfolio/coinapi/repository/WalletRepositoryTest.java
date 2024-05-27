package com.portfolio.coinapi.repository;


import com.portfolio.coinapi.model.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static com.portfolio.coinapi.commons.WalletConstants.WALLET;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WalletRepositoryTest {

    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        WALLET.setId(null);
    }

    @Test
    public void createWallet_withValidData_returnsWallet() {


        Wallet wallet = walletRepository.save(WALLET);

        Wallet sut = testEntityManager.find(Wallet.class, wallet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getClient().getEmail()).isEqualTo(WALLET.getClient().getEmail());
        assertThat(sut.getId()).isEqualTo(wallet.getId());
    }

    @Test
    public void getWallet_byExistingId_returnsWallet() {
        Wallet wallet = walletRepository.save(WALLET);
        Optional<Wallet> sut = walletRepository.findById(wallet.getId());

        assertThat(sut).isNotEmpty();
        assertThat(sut).isNotNull();
        assertThat(sut.get().getClient().getEmail()).isEqualTo(WALLET.getClient().getEmail());
    }

    @Test
    public void getWallet_byUnexistingId_returnsNotFound() {
        Optional<Wallet> sut = walletRepository.findById(1L);
        assertThat(sut).isEmpty();
    }



}
