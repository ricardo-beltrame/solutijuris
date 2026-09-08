package com.solutijuris.dto;

import jakarta.validation.constraints.NotBlank;

public record PerfilUpdateRequest(
        @NotBlank(message = "Nome é obrigatório") String nomeCompleto,
        String telefone,
        String senhaAtual,
        String novaSenha
) {}