package com.example.back_PFE.controller;

import com.example.back_PFE.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/dashboard")
public class DashboardController {
    @GetMapping
    public ResponseEntity<String> redirectUser(@RequestHeader("Authorization") String token, JwtUtil jwtUtil) {
        String role = jwtUtil.extractClaims(token.substring(7)).get("role", String.class);

        switch (role) {
            case "CANDIDAT":
                return ResponseEntity.ok("Rediriger vers interface CANDIDAT");
            case "CLIENT":
                return ResponseEntity.ok("Rediriger vers interface CLIENT");
            case "ADMIN":
                return ResponseEntity.ok("Rediriger vers interface ADMIN");
            default:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
