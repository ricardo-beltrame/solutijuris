package com.solutijuris.service;

import com.solutijuris.dto.AuthResponse;
import com.solutijuris.dto.LoginRequest;
import com.solutijuris.dto.RegisterRequest;
import com.solutijuris.exception.CredenciaisInvalidasException;
import com.solutijuris.model.entity.LogAuditoria;
import com.solutijuris.model.entity.Usuario;
import com.solutijuris.repository.LogAuditoriaRepository;
import com.solutijuris.repository.UsuarioRepository;
import com.solutijuris.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponse cadastrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        var usuario = new Usuario();
        usuario.setNomeCompleto(request.nomeCompleto());
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(request.perfil());

        usuarioRepository.save(usuario);

        registrarLog(usuario.getEmail(), "CADASTRO", "Usuario",
                usuario.getId(), null,
                "Cadastro de usuário com perfil " + request.perfil());

        String token = jwtProvider.generateToken(
                usuario.getEmail(), usuario.getPerfil());

        return new AuthResponse(token, usuario.getNomeCompleto(),
                usuario.getEmail(), usuario.getPerfil().name());
    }

    @Transactional
    public AuthResponse autenticar(LoginRequest request) {
        var usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos"));

        // <-- NOVO: bloqueia se já atingiu o limite de tentativas
        if (usuario.getTentativasLogin() >= 5) {
            throw new RuntimeException(
                    "Conta bloqueada por 15 minutos devido a múltiplas tentativas inválidas");
        }

        if (usuario.getBloqueadoAte() != null
                && usuario.getBloqueadoAte().isAfter(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Conta bloqueada temporariamente. Tente novamente mais tarde.");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            usuario.setTentativasLogin(usuario.getTentativasLogin() + 1);

            if (usuario.getTentativasLogin() >= 5) {
                usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(15));
                usuario.setTentativasLogin(0);

                registrarLog(usuario.getEmail(), "BLOQUEIO", "Usuario",
                        usuario.getId(), null,
                        "Conta bloqueada por 15 min após 5 tentativas falhas");
            }

            usuarioRepository.save(usuario);
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos");
        }

        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        registrarLog(usuario.getEmail(), "LOGIN", "Usuario",
                usuario.getId(), null, "Login realizado com sucesso");

        String token = jwtProvider.generateToken(
                usuario.getEmail(), usuario.getPerfil());

        return new AuthResponse(token, usuario.getNomeCompleto(),
                usuario.getEmail(), usuario.getPerfil().name());
    }

    private void registrarLog(String usuarioEmail, String acao,
                              String entidade, java.util.UUID entidadeId,
                              String dadosAnteriores, String dadosNovos) {
        var log = new LogAuditoria();
        log.setUsuarioEmail(usuarioEmail);
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setDadosAnteriores(dadosAnteriores);
        log.setDadosNovos(dadosNovos);
        logAuditoriaRepository.save(log);
    }
}