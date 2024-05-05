package com.portfolio.coinapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@NoArgsConstructor
@Getter
@Setter
@Entity()
@Table(name = "TB_CLIENT")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotNull
    @Column(unique = true)
    private String username;
    @NotBlank
    @NotNull
    private String password;
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonManagedReference
    @NotNull
    private Wallet wallet;

    public Client(String username, String password, Wallet wallet) {
        this.username = username;
        this.password = password;
        this.wallet = wallet;
    }

    public Client(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
