package com.portfolio.coinapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "TB_CLIENT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    @NotNull
    @Column(unique = true)
    private String username;
    @NotBlank
    @NotNull
    private String password;
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Wallet wallet;

    public Client(String username, String password, Wallet wallet) {
        this.username = username;
        this.password = password;
    }

}
