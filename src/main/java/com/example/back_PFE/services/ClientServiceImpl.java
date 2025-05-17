package com.example.back_PFE.services;

import com.example.back_PFE.entities.Client;
import com.example.back_PFE.entities.Statusdoss;
import com.example.back_PFE.repository.ClientRepo;
import com.example.back_PFE.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepo clientRepo;
    @Override
    public Client addClient(Client client ,long userId) {
        User user = new User();  // Crée un user vide
        user.setId(userId);
        client.setNom(client.getNom());
        client.setPrenom(client.getPrenom());
        client.setEmail(client.getEmail());
        client.setPhone(client.getPhone());
        client.setSpecialite(client.getSpecialite());
        client.setNiveau(client.getNiveau());
        client.setAdress(client.getAdress());
        if (client.getStatus() == null) {
            client.setStatus(Statusdoss.Depose);
        }
        client.setUser(user);
        return clientRepo.save(client);
    }


    @Override
        public List<Client> getAllClients() {
            return clientRepo.findAll();
        }
    @Override
    public void updateClientStatus(Long id_client, Statusdoss status) {
        Client client = clientRepo.findById(id_client)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        client.setStatus(status);
        clientRepo.save(client);
    }


    @Override
    public Optional<Client> getClientById(Long id_client) {
        return clientRepo.findById(id_client);
    }



}


