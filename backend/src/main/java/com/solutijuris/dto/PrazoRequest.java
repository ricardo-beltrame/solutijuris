package com.solutijuris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record PrazoRequest(

        @NotNull UUID processoId,

        @NotNull UUID responsavelId,

        @NotNull LocalDate dataVencimento,

        @NotBlank String descricao
) {}