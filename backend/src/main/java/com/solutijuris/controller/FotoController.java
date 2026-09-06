package com.solutijuris.controller;

import com.solutijuris.model.entity.Usuario;
import com.solutijuris.repository.UsuarioRepository;
import com.solutijuris.security.JwtProvider;
import com.solutijuris.service.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class FotoController {

    private final UsuarioRepository usuarioRepository;
    private final CloudinaryService cloudinaryService;
    private final JwtProvider jwtProvider;

    @PostMapping("/foto")
    @Transactional
    public ResponseEntity<?> uploadFoto(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        try {
            String token = extractToken(request);
            String email = jwtProvider.getEmailFromToken(token);

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Deleta foto anterior se existir
            if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
                String oldPublicId = cloudinaryService.extractPublicId(usuario.getFotoUrl());
                if (oldPublicId != null) {
                    cloudinaryService.delete("solutijuris/avatars/" + oldPublicId);
                }
            }

            // Upload nova foto
            String fotoUrl = cloudinaryService.upload(file, "avatars");
            usuario.setFotoUrl(fotoUrl);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of("fotoUrl", fotoUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/foto")
    @Transactional
    public ResponseEntity<?> removerFoto(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            String email = jwtProvider.getEmailFromToken(token);

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (usuario.getFotoUrl() != null) {
                String publicId = cloudinaryService.extractPublicId(usuario.getFotoUrl());
                if (publicId != null) {
                    cloudinaryService.delete("solutijuris/avatars/" + publicId);
                }
                usuario.setFotoUrl(null);
                usuarioRepository.save(usuario);
            }

            return ResponseEntity.ok(Map.of("message", "Foto removida"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/foto")
    public ResponseEntity<?> getFoto(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            String email = jwtProvider.getEmailFromToken(token);

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            return ResponseEntity.ok(Map.of(
                    "fotoUrl", usuario.getFotoUrl() != null ? usuario.getFotoUrl() : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token não encontrado");
    }
}