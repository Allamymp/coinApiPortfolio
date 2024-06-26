package com.portfolio.coinapi.repository;

import com.portfolio.coinapi.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String username);
    boolean existsByEmail(String username);
    Optional<Client> findByUniqueToken(String uniqueToken);

}
