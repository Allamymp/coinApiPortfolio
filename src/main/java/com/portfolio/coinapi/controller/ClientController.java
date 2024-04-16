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

@RestController
@RequestMapping("/client")
@CrossOrigin("*")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @PostMapping("/create")
    public ResponseEntity<Client> create(@Valid @RequestBody Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(client));
    }

    @GetMapping("/findById")
    public ResponseEntity<Client> findById(@Valid @RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(clientService.findById(id));
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<Client> findByUsername(@Valid @RequestParam String username) {
        return ResponseEntity.status(HttpStatus.OK).body(clientService.findByUsername(username));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Client>> findAll(@PageableDefault(size = 10, sort = {"username"}) Pageable pageable) {
        return ResponseEntity.ok().body(clientService.allClients(pageable));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Client client) {
        clientService.update(client);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@Valid @PathVariable Long id) {
        clientService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
