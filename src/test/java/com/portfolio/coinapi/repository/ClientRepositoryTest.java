package com.portfolio.coinapi.repository;


import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.model.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static com.portfolio.coinapi.commons.ClientConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class ClientRepositoryTest {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        CLIENT.setId(null);
        CLIENT.setWallet(new Wallet());
    }

    @Test
    public void createClient_withValidData_returnsClient() {

        CLIENT.setWallet(testEntityManager.persistAndFlush(new Wallet()));
        Client client = clientRepository.save(CLIENT);


        Client sut = testEntityManager.find(Client.class, client.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getEmail()).isEqualTo(CLIENT.getEmail());
        assertThat(sut.getPassword()).isEqualTo(CLIENT.getPassword());
    }

    @Test
    public void createClient_withInvalidData_ThrowsException() {
        //Arrange
        //Act
        //Assert
        assertThatThrownBy(() -> clientRepository.save(EMPTY_CLIENT)).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> clientRepository.save(INVALID_CLIENT)).isInstanceOf(Exception.class);

    }

    @Test
    public void getClient_byExistingName_returnsClient() {
        Client client = clientRepository.save(CLIENT);
        Optional<Client> sut = clientRepository.findByEmail(CLIENT.getEmail());
        assertThat(sut).isPresent();
        assertThat(sut.get().getEmail()).isEqualTo(client.getEmail());
        assertThat(sut.get().getPassword()).isEqualTo(client.getPassword());
    }

    @Test
    public void getClient_byUnexistingName_returnsEmpty() {
        Optional<Client> sut = clientRepository.findByEmail(CLIENT.getEmail());
        assertThat(sut).isEmpty();
    }

    @Test
    public void getClient_byExistingId_returnsClient() {
        Client client = clientRepository.save(CLIENT);

        Optional<Client> sut = clientRepository.findById(client.getId());

        assertThat(sut).isNotNull();
        assertThat(sut).isNotEmpty();
        assertThat(sut.get().getId()).isEqualTo(client.getId());
        assertThat(sut.get().getEmail()).isEqualTo(client.getEmail());
        assertThat(sut.get().getPassword()).isEqualTo(client.getPassword());
    }

    @Test
    public void getClient_byUnexistingId_returnsNotFound() {

        Optional<Client> sut = clientRepository.findById(1L);
        assertThat(sut).isEmpty();
    }

    @Test
    public void listClients_returnsClientList() {
        List<Client> clientList = CLIENT_LIST;

        testEntityManager.persist(clientList.get(0));
        testEntityManager.persist(clientList.get(1));
        testEntityManager.persist(clientList.get(2));

        List<Client> sut = clientRepository.findAll();

        assertThat(sut).hasSize(3);
        assertThat(sut.getFirst().getEmail()).isEqualTo("firstClient");
        assertThat(sut.getLast().getEmail()).isEqualTo("thirdClient");
    }

    @Test
    public void listClients_returnsNoClients() {

        List<Client> sut = clientRepository.findAll();

        assertThat(sut).isEmpty();
    }
}
