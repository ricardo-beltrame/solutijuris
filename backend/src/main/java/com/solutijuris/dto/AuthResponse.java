package com.solutijuris.dto;

public record AuthResponse(
        String token,
        String nome,
        String email,
        String perfil
) {}