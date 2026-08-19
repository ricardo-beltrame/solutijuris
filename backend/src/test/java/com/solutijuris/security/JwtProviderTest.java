package com.solutijuris.security;

import com.solutijuris.model.enums.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setup() {
        jwtProvider = new JwtProvider(
                "chave-super-secreta-solutijuris-2026-para-teste-com-32-caracteres",
                3600000L);
    }

    @Test
    @DisplayName("Deve gerar token e extrair o e-mail")
    void deveGerarTokenEExtrairEmail() {
        String token = jwtProvider.generateToken(
                "ricardo@solutijuris.com", PerfilUsuario.ADVOGADO);
        assertNotNull(token);
        assertEquals("ricardo@solutijuris.com",
                jwtProvider.getEmailFromToken(token));
    }

    @Test
    @DisplayName("Deve extrair o perfil do token")
    void deveExtrairPerfil() {
        String token = jwtProvider.generateToken(
                "ricardo@solutijuris.com", PerfilUsuario.ADVOGADO);
        assertEquals("ADVOGADO", jwtProvider.getPerfilFromToken(token));
    }

    @Test
    @DisplayName("Deve validar token correto")
    void deveValidarTokenCorreto() {
        String token = jwtProvider.generateToken(
                "ricardo@solutijuris.com", PerfilUsuario.ADVOGADO);
        assertTrue(jwtProvider.validateToken(token));
    }

    @Test
    @DisplayName("Deve rejeitar token inválido")
    void deveRejeitarTokenInvalido() {
        assertFalse(jwtProvider.validateToken("token.invalido.aqui"));
    }
}