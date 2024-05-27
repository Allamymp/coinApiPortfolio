package com.portfolio.coinapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TB_CLIENT")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @NotNull
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @NotNull
    private String password;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Wallet wallet;

    public Client(String email, String password, Wallet wallet) {
        this.email = email;
        this.password = password;
        this.wallet = wallet;
    }

    public Client(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
