package com.solutijuris.repository;

import com.solutijuris.model.entity.Processo;
import com.solutijuris.model.enums.StatusProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, UUID> {

    Optional<Processo> findByNumeroUnico(String numeroUnico);

    boolean existsByNumeroUnico(String numeroUnico);

    List<Processo> findByResponsavelId(UUID responsavelId);

    List<Processo> findByStatus(StatusProcesso status);

    List<Processo> findBySegredoJusticaFalse();
}