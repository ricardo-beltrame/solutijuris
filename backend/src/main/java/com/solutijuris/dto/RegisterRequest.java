package com.solutijuris.dto;

import com.solutijuris.model.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank String nomeCompleto,
        @NotBlank @Email String email,
        @NotBlank String senha,
        @NotNull PerfilUsuario perfil
) {}