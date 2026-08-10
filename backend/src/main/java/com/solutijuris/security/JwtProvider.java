package com.solutijuris.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.solutijuris.model.enums.PerfilUsuario;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey chave;
    private final long expiracao;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiracao) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracao = expiracao;
    }

    public String generateToken(String email, PerfilUsuario perfil) {
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + expiracao);

        return Jwts.builder()
                .setSubject(email)
                .claim("perfil", perfil.name())
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(chave, SignatureAlgorithm.HS384)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(chave)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getPerfilFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(chave)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("perfil", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(chave)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}