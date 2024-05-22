package com.portfolio.coinapi.controller;


import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.service.ClientService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger clientControllerLogger = LogManager.getLogger(ClientController.class);

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @PostMapping("/create")
    public ResponseEntity<Client> create(@Valid @RequestBody Client client) {
        clientControllerLogger.info("Received request to create client: {}", client);
        Client createdClient = clientService.create(client);
        clientControllerLogger.info("Client created successfully: {}", createdClient);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }

    @GetMapping("/findById")
    public ResponseEntity<Client> findById(@Valid @RequestParam Long id) {
        clientControllerLogger.info("Received request to find client by id: {}", id);
        Client client = clientService.findById(id);
        if (client != null) {
            clientControllerLogger.info("Client found for id {}: {}", id, client);
            return ResponseEntity.status(HttpStatus.OK).body(client);
        } else {
            clientControllerLogger.info("Client received is null: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<Client> findByUsername(@Valid @RequestParam String username) {
        clientControllerLogger.info("Received request to find client by username: {}", username);
        Client client = clientService.findByUsername(username);
        if (client != null) {
            clientControllerLogger.info("Client found for username {}: {}", username, client);
            return ResponseEntity.status(HttpStatus.OK).body(client);
        } else {
            clientControllerLogger.info("Client received is null: {}", username);
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/all")
    public ResponseEntity<Page<Client>> findAll(@PageableDefault(sort = {"username"}) Pageable pageable) {
        clientControllerLogger.info("Received request to fetch all clients");
        Page<Client> clientsPage = clientService.allClients(pageable);
        clientControllerLogger.info("Returned {} clients", clientsPage.getTotalElements());
        return ResponseEntity.ok().body(clientsPage);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody Client client) {
        clientControllerLogger.info("Received request to update client: {}", client);
        clientService.update(client);
        clientControllerLogger.info("Client updated successfully: {}", client);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@Valid @PathVariable Long id) {
        clientControllerLogger.info("Received request to delete client by id: {}", id);
        clientService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
