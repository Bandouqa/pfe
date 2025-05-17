package com.example.back_PFE.services;

import com.example.back_PFE.repository.ServiceRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceServiceImpl implements ServiceService{
    @Autowired
    ServiceRepo serviceRepo ;
    @Override
    public com.example.back_PFE.entities.Service addService(com.example.back_PFE.entities.Service service, String titre) {
        service.setImage(service.getImage());
        service.setTitre(service.getTitre());
        service.setDescription(service.getDescription());
        return serviceRepo.save(service);
    }
    @Override
    public com.example.back_PFE.entities.Service updateService(com.example.back_PFE.entities.Service service, long id) {
        return serviceRepo.findById(id).map(old -> {
            old.setImage(service.getImage());
            old.setTitre(service.getTitre());
            old.setDescription(service.getDescription());

            return serviceRepo.save(old);
        }).orElseThrow(() -> new EntityNotFoundException("Service with ID " + id + " not found"));

    }
    @Override
    public List<com.example.back_PFE.entities.Service> getAll() {
        return serviceRepo.findAll();
    }

    @Override
    public Map<String, Boolean> deleteService(long id) {
        serviceRepo.deleteById(id);
        Map<String,Boolean> res = new HashMap<>();
        res.put("deleted",Boolean.TRUE);
        return res;
    }

    @Override
    public Optional<com.example.back_PFE.entities.Service> getService(long id) {
        return serviceRepo.findById(id);
    }

}
