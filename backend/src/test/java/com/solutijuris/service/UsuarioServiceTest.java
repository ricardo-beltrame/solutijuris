package com.solutijuris.service;

import com.solutijuris.dto.LoginRequest;
import com.solutijuris.dto.RegisterRequest;
import com.solutijuris.model.entity.Usuario;
import com.solutijuris.model.enums.PerfilUsuario;
import com.solutijuris.repository.LogAuditoriaRepository;
import com.solutijuris.repository.UsuarioRepository;
import com.solutijuris.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @Mock
    private JwtProvider jwtProvider;

    private PasswordEncoder passwordEncoder;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        usuarioService = new UsuarioService(
                usuarioRepository,
                logAuditoriaRepository,
                passwordEncoder,
                jwtProvider
        );
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void deveCadastrarUsuario() {
        var request = new RegisterRequest(
                "Dr. Ricardo",
                "ricardo@solutijuris.com",
                "123456",
                PerfilUsuario.ADVOGADO
        );

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtProvider.generateToken(anyString(), any(PerfilUsuario.class)))
                .thenReturn("eyJhbGciOiJIUzM4NCJ9.token123");

        var response = usuarioService.cadastrar(request);

        assertNotNull(response);
        assertEquals("Dr. Ricardo", response.nome());
        assertEquals("ricardo@solutijuris.com", response.email());
        assertEquals("ADVOGADO", response.perfil());
        assertTrue(response.token().startsWith("eyJ"));
    }

    @Test
    @DisplayName("Deve lançar erro ao cadastrar e-mail duplicado")
    void deveLancarErroEmailDuplicado() {
        var request = new RegisterRequest(
                "Admin",
                "admin@solutijuris.com",
                "123456",
                PerfilUsuario.ADMIN
        );

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        var exception = assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(request));

        assertEquals("E-mail já cadastrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void deveAutenticarUsuarioValido() {
        var email = "admin@solutijuris.com";
        var senha = "123456";
        var request = new LoginRequest(email, senha);

        var usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuario.setAtivo(true);
        usuario.setTentativasLogin(0);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(jwtProvider.generateToken(anyString(), any(PerfilUsuario.class)))
                .thenReturn("eyJhbGciOiJIUzM4NCJ9.token123");

        var response = usuarioService.autenticar(request);

        assertNotNull(response);
        assertEquals(email, response.email());
        assertEquals("ADMIN", response.perfil());
    }

    @Test
    @DisplayName("Deve bloquear após 5 tentativas inválidas")
    void deveBloquearApos5Tentativas() {
        var email = "admin@solutijuris.com";
        var request = new LoginRequest(email, "senha_errada");

        var usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode("senha_correta"));
        usuario.setAtivo(true);
        usuario.setTentativasLogin(5);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        var exception = assertThrows(RuntimeException.class,
                () -> usuarioService.autenticar(request));

        assertTrue(exception.getMessage().contains("bloqueada"));
    }

    @Test
    @DisplayName("Deve rejeitar login com senha inválida")
    void deveRejeitarSenhaInvalida() {
        var email = "admin@solutijuris.com";
        var request = new LoginRequest(email, "senha_errada");

        var usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode("senha_correta"));
        usuario.setAtivo(true);
        usuario.setTentativasLogin(0);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        var exception = assertThrows(RuntimeException.class,
                () -> usuarioService.autenticar(request));

        assertEquals("E-mail ou senha inválidos", exception.getMessage());
    }
}