package com.solutijuris.repository;

import com.solutijuris.model.entity.SenhaResetToken;
import com.solutijuris.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface SenhaResetTokenRepository extends JpaRepository<SenhaResetToken, Long> {
    Optional<SenhaResetToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUsuario(Usuario usuario);
}