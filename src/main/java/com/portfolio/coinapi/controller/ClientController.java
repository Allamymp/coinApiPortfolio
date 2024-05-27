package com.portfolio.coinapi.controller;

import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/client")
@CrossOrigin("*")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@Valid @RequestBody Client client) {
        URI location = clientService.create(client);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/findById")
    public ResponseEntity<Client> findById(@Valid @RequestParam Long id) {
        return clientService.findById(id);
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<Client> findByUsername(@Valid @RequestParam String username) {
        return clientService.findByUsername(username);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Client>> findAll(@PageableDefault(sort = {"username"}) Pageable pageable) {
        return ResponseEntity.ok().body(clientService.allClients(pageable));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Client client) {
        clientService.updateClient(client);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@Valid @PathVariable Long id) {
        clientService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
