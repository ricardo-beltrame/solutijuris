package com.solutijuris;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutijuris.dto.LoginRequest;
import com.solutijuris.dto.RegisterRequest;
import com.solutijuris.model.enums.PerfilUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Fluxo completo: cadastro, login e acesso autenticado")
    void fluxoCompletoAutenticacao() throws Exception {
        // 1. Cadastro
        var register = new RegisterRequest(
                "Dr. Ricardo",
                "ricardo@solutijuris.com",
                "123456",
                PerfilUsuario.ADVOGADO
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ricardo@solutijuris.com"))
                .andExpect(jsonPath("$.perfil").value("ADVOGADO"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        // 2. Login
        var login = new LoginRequest("ricardo@solutijuris.com", "123456");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        // 3. Extrai o token do JSON retornado
        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token")
                .asText();

        // 4. Acessa um endpoint protegido com o token
        mockMvc.perform(get("/processos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve rejeitar acesso sem token")
    void deveRejeitarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/processos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve rejeitar login com senha errada")
    void deveRejeitarLoginSenhaErrada() throws Exception {
        var login = new LoginRequest("ricardo@solutijuris.com", "senha_errada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}