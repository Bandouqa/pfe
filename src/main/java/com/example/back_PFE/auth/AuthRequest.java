package com.example.back_PFE.auth;

import com.example.back_PFE.user.Role;
import lombok.Data;

@Data
public class AuthRequest {

    private String email;
    private String password;
    private Role role;
}

