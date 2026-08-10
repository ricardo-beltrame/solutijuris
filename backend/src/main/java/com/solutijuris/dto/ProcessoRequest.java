package com.solutijuris.dto;

import com.solutijuris.model.enums.AreaDireito;
import com.solutijuris.model.enums.Tribunal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProcessoRequest(

        @NotBlank
        @Size(min = 20, max = 25)
        @Pattern(regexp = "[0-9]{7}-[0-9]{2}[.][0-9]{4}[.][0-9][.][0-9]{2}[.][0-9]{4}")
        String numeroUnico,

        @NotNull AreaDireito areaDireito,

        @NotNull Tribunal tribunal,

        @NotBlank String vara,

        String assunto,

        LocalDate dataDistribuicao,

        boolean segredoJustica,

        BigDecimal valorCausa,

        String poloAtivo,

        String poloPassivo,

        @NotNull UUID responsavelId
) {}