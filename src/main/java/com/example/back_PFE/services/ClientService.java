package com.example.back_PFE.services;

import com.example.back_PFE.entities.Client;
import com.example.back_PFE.entities.Statusdoss;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client addClient(Client client,long userId);

    List<Client> getAllClients();

    void updateClientStatus(Long id_client, Statusdoss statusdoss);


    Optional<Client> getClientById(Long id_client);
}
