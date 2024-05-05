package com.portfolio.coinapi.repository;


import com.portfolio.coinapi.model.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
    }

    @Test
    public void createClient_withValidData_returnsClient() {
        Client client = clientRepository.save(CLIENT);

        Client sut = testEntityManager.find(Client.class, client.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getUsername()).isEqualTo(CLIENT.getUsername());
        assertThat(sut.getPassword()).isEqualTo(CLIENT.getPassword());
    }

    @Test
    public void createClient_WithInvalidData_ThrowsException() {
        //Arrange
        //Act
        //Assert
        assertThatThrownBy(() -> clientRepository.save(EMPTY_CLIENT)).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> clientRepository.save(INVALID_CLIENT)).isInstanceOf(Exception .class);

    }
}
