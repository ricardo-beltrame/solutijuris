package com.solutijuris.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CnjValidatorTest {

    private final CnjValidator validator = new CnjValidator();

    @Test
    @DisplayName("Deve aceitar número CNJ válido")
    void deveAceitarNumeroValido() {
        assertTrue(validator.validar("7250057-21.2026.5.18.1540"));
    }

    @Test
    @DisplayName("Deve aceitar número CNJ real válido")
    void deveAceitarNumeroRealValido() {
        assertTrue(validator.validar("2050028-67.2026.2.00.8952"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567-88.2024.8.26.0100",
            "1234567-30.2024.8.26.0100",
            "1234567-29.2024.8.26.0100",
            ""
    })
    @DisplayName("Deve rejeitar números CNJ inválidos")
    void deveRejeitarNumerosInvalidos(String numeroInvalido) {
        assertFalse(validator.validar(numeroInvalido));
    }

    @Test
    @DisplayName("Deve rejeitar null")
    void deveRejeitarNull() {
        assertFalse(validator.validar(null));
    }

    @Test
    @DisplayName("Deve rejeitar formato incorreto")
    void deveRejeitarFormatoIncorreto() {
        assertFalse(validator.validar("1234567-31.2024.8.26.010"));
        assertFalse(validator.validar("1234567-31.2024.8.26.01000"));
        assertFalse(validator.validar("1234567-31.2024.8.26.01A0"));
    }
}