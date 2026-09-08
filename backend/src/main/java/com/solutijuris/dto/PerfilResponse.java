package com.solutijuris.dto;

public record PerfilResponse(
        String nome,
        String email,
        String telefone,
        String fotoUrl
) {}