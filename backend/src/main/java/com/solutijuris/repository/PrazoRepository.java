package com.solutijuris.repository;

import com.solutijuris.model.entity.Prazo;
import com.solutijuris.model.enums.StatusPrazo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrazoRepository extends JpaRepository<Prazo, UUID> {

    List<Prazo> findByProcessoId(UUID processoId);

    List<Prazo> findByResponsavelId(UUID responsavelId);

    List<Prazo> findByStatus(StatusPrazo status);

    List<Prazo> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT p FROM Prazo p WHERE p.status = 'ABERTO' AND p.dataVencimento < :hoje")
    List<Prazo> findPrazosVencidos(@Param("hoje") LocalDate hoje);

    @Query("SELECT p FROM Prazo p WHERE p.status = 'ABERTO' AND p.dataVencimento = :data")
    List<Prazo> findPrazosVencendoEm(@Param("data") LocalDate data);
}