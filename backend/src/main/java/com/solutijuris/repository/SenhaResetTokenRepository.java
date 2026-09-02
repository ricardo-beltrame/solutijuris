package com.solutijuris.repository;

import com.solutijuris.model.entity.SenhaResetToken;
import com.solutijuris.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SenhaResetTokenRepository extends JpaRepository<SenhaResetToken, Long> {
    Optional<SenhaResetToken> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}