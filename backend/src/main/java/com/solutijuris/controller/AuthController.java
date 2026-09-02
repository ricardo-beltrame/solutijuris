package com.solutijuris.controller;

import com.solutijuris.dto.AuthResponse;
import com.solutijuris.dto.LoginRequest;
import com.solutijuris.dto.RegisterRequest;
import com.solutijuris.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.solutijuris.dto.RecuperarSenhaRequest;
import com.solutijuris.dto.RedefinirSenhaRequest;
import com.solutijuris.model.entity.SenhaResetToken;
import com.solutijuris.repository.SenhaResetTokenRepository;
import com.solutijuris.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import com.solutijuris.dto.MensagemResponse;
import com.solutijuris.repository.UsuarioRepository;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SenhaResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        var response = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        var response = usuarioService.autenticar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<MensagemResponse> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequest request) {
        var usuario = usuarioRepository.findByEmail(request.getEmail());

        if (usuario.isPresent()) {
            // Invalida tokens anteriores
            tokenRepository.deleteByUsuario(usuario.get());

            // Gera novo token
            SenhaResetToken token = new SenhaResetToken();
            token.setUsuario(usuario.get());
            token = tokenRepository.save(token);

            // Envia email
            emailService.enviarEmailRecuperacao(request.getEmail(), token.getToken());
        }

        // Sempre retorna sucesso — não revela se o email existe
        return ResponseEntity.ok(new MensagemResponse("Se o e-mail estiver cadastrado, você receberá as instruções."));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<MensagemResponse> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        var tokenOpt = tokenRepository.findByToken(request.getToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MensagemResponse("Token inválido."));
        }

        var token = tokenOpt.get();

        if (!token.isValido()) {
            return ResponseEntity.badRequest().body(new MensagemResponse("Token expirado ou já utilizado."));
        }

        // Atualiza a senha
        var usuario = token.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);

        // Marca token como usado
        token.setUsado(true);
        tokenRepository.save(token);

        return ResponseEntity.ok(new MensagemResponse("Senha redefinida com sucesso!"));
    }
}