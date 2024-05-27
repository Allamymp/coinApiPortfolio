package com.portfolio.coinapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.coinapi.model.Client;
import com.portfolio.coinapi.service.ClientService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.portfolio.coinapi.commons.ClientConstants.CLIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @MockBean
    private Logger clientControllerLogger;

    @Test
    public void createClient_withValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/client/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CLIENT)))
                .andExpect(status().isCreated());

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientControllerLogger).info(eq("Received request to create client: {}"), clientCaptor.capture());

        Client sut = clientCaptor.getValue();
        assertEquals(CLIENT.getEmail(), sut.getEmail());
        assertEquals(CLIENT.getPassword(), sut.getPassword());
        assertNotNull(sut.getWallet());
    }

}
