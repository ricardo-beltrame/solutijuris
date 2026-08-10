package com.solutijuris.model.entity;

import com.solutijuris.model.enums.StatusPrazo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "prazos")
public class Prazo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_cumprimento")
    private LocalDate dataCumprimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusPrazo status;

    @Column(nullable = false, length = 255)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Usuario responsavel;

    @Column(name = "notificado_15_dias")
    private boolean notificado15Dias;

    @Column(name = "notificado_7_dias")
    private boolean notificado7Dias;

    @Column(name = "notificado_3_dias")
    private boolean notificado3Dias;

    @Column(name = "notificado_1_dia")
    private boolean notificado1Dia;

    @Column(name = "notificado_vencido")
    private boolean notificadoVencido;
}