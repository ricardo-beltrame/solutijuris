package com.solutijuris.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RecuperarSenhaRequest {
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}