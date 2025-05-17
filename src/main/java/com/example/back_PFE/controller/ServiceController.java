package com.example.back_PFE.controller;

import com.example.back_PFE.entities.Evenement;
import com.example.back_PFE.entities.Service;
import com.example.back_PFE.jwt.JwtUtil;
import com.example.back_PFE.services.EvenementService;
import com.example.back_PFE.services.ServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping(value = "/Service")
@CrossOrigin(origins = "http://localhost:4200")
public class ServiceController {
    private final JwtUtil jwtUtil;
    private final ServiceService serviceService;

    public ServiceController(JwtUtil jwtUtil,ServiceService serviceService){
        this.serviceService = serviceService;
        this.jwtUtil=jwtUtil;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addService(@RequestBody Service service, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            // Extraire le token JWT et obtenir l'email
            String token = authorizationHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            System.out.println("Email extrait : " + email);

            Service newService = serviceService.addService(service,email);

            return new ResponseEntity<>(newService, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(value = "/{id}")
    public Service updateService(@RequestBody Service service, @PathVariable("id") long id) {
        return serviceService.updateService(service, id);
    }

    @GetMapping
    public List<Service> getAll() {
        return serviceService.getAll();
    }

    @DeleteMapping(value = "/{id}")
    public Map<String, Boolean> deleteService(@PathVariable("id") long id) {
        return serviceService.deleteService(id);
    }
    @GetMapping (value = "/{id}")
    public Optional<Service> getService(@PathVariable("id") long id)
    {
        return serviceService.getService(id);
    }
}
