package com.example.back_PFE.services;

import com.example.back_PFE.entities.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceService {
    com.example.back_PFE.entities.Service addService(com.example.back_PFE.entities.Service service, String titre);

    com.example.back_PFE.entities.Service updateService(com.example.back_PFE.entities.Service service, long id);

    List<Service> getAll();

    Map<String, Boolean> deleteService(long id);

    Optional<Service> getService(long id);
}
