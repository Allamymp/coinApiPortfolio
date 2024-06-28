package com.portfolio.coinapi.repository;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Coin;
import com.portfolio.coinapi.model.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static com.portfolio.coinapi.commons.CoinConstants.COIN;
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
        COIN.setId(null);
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

    @Test
    public void updateWallet_withValidData_returnsUpdatedWallet() {
        Wallet wallet = walletRepository.save(WALLET);
        wallet.setClient(new Client("new.email@example.com", "newPassword"));
        Wallet updatedWallet = walletRepository.save(wallet);

        Wallet sut = testEntityManager.find(Wallet.class, updatedWallet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getClient().getEmail()).isEqualTo("new.email@example.com");
    }

    @Test
    public void deleteWallet_byExistingId_deletesWallet() {
        Wallet wallet = walletRepository.save(WALLET);
        walletRepository.deleteById(wallet.getId());

        Optional<Wallet> sut = walletRepository.findById(wallet.getId());
        assertThat(sut).isEmpty();
    }

    @Test
    public void deleteWallet_byUnexistingId_doesNothing() {
        long unexisting = 999;

        walletRepository.deleteById(unexisting);
    }

    @Test
    public void addCoin_toWallet_updatesWalletWithCoin() {
        Wallet wallet = walletRepository.save(WALLET);
        Coin coin = testEntityManager.persist(COIN);

        wallet.addCoin(coin);
        Wallet updatedWallet = walletRepository.save(wallet);

        Wallet sut = testEntityManager.find(Wallet.class, updatedWallet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getCoinList()).contains(coin);
    }

    @Test
    public void removeCoin_fromWallet_updatesWalletWithoutCoin() {
        Wallet wallet = walletRepository.save(WALLET);
        Coin coin = testEntityManager.persist(COIN);

        wallet.addCoin(coin);
        walletRepository.save(wallet);

        wallet.removeCoin(coin);
        Wallet updatedWallet = walletRepository.save(wallet);

        Wallet sut = testEntityManager.find(Wallet.class, updatedWallet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getCoinList()).doesNotContain(coin);
    }
}
