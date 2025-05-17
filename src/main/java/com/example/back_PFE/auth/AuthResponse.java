package com.example.back_PFE.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
   /* private String nom;
    private String prenom; */
    private String accessToken;
    private String refreshToken;
    private String role;
    private Long id;
}
