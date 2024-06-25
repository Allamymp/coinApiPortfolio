package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.service.ClientService;
import com.portfolio.coinapi.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/clients")
@CrossOrigin("*")
public class ClientController {

    private final ClientService clientService;
    private final EmailService emailService;

    public ClientController(ClientService clientService, EmailService emailService) {
        this.clientService = clientService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Client client) {
        URI location = clientService.create(client);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(@Valid @PathVariable Long id) {
        return clientService.findById(id);
    }

    @GetMapping("/username")
    public ResponseEntity<Client> findByUsername(@RequestParam String username) {
        return clientService.findByUsername(username);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Client>> findAll(@PageableDefault(sort = {"username"}) Pageable pageable) {
        return ResponseEntity.ok().body(clientService.allClients(pageable));
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody Client client) {
        clientService.updateClient(client);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reset/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token) {
        String[] info = clientService.resetPassword(token);
        if (info != null) {
            emailService.sendResetPasswordConfirmation(info[0], info[1]);
            return ResponseEntity.ok().body("New password send to email!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token. Please request a new password reset link.");

        }
    }

    @GetMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestParam String email) {
        clientService.forgetPassword(email);
        return ResponseEntity.ok().body("Password instructions sent to your email.");
    }
}
