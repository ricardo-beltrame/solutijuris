package com.solutijuris.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutijuris.config.SecurityConfig;
import com.solutijuris.dto.LoginRequest;
import com.solutijuris.dto.RegisterRequest;
import com.solutijuris.dto.AuthResponse;
import com.solutijuris.model.enums.PerfilUsuario;
import com.solutijuris.security.JwtProvider;
import com.solutijuris.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.solutijuris.service.EmailService;
import com.solutijuris.repository.SenhaResetTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.solutijuris.repository.UsuarioRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private SenhaResetTokenRepository senhaResetTokenRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UsuarioRepository usuarioRepository;


    @Test
    @DisplayName("POST /auth/register - deve retornar 201")
    void deveRegistrarUsuario() throws Exception {
        var request = new RegisterRequest("Admin", "admin@teste.com", "123456", PerfilUsuario.ADMIN);
        var response = new AuthResponse("token123", "Admin", "admin@teste.com", "ADMIN");

        when(usuarioService.cadastrar(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin@teste.com"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /auth/login - deve retornar 200")
    void deveAutenticarUsuario() throws Exception {
        var request = new LoginRequest("admin@teste.com", "123456");
        var response = new AuthResponse("token123", "Admin", "admin@teste.com", "ADMIN");

        when(usuarioService.autenticar(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    @DisplayName("POST /auth/register - deve retornar 422 para dados inválidos")
    void deveRejeitarDadosInvalidos() throws Exception {
        var request = new RegisterRequest("", "email_invalido", "", null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());  // <-- 422 em vez de 400
    }
}