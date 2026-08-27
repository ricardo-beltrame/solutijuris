package com.solutijuris.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.solutijuris.model.enums.AreaDireito;
import com.solutijuris.model.enums.StatusProcesso;
import com.solutijuris.model.enums.Tribunal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "processos")
public class Processo extends BaseEntity {

    @Column(name = "numero_unico", nullable = false, unique = true, length = 25)
    private String numeroUnico;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_direito", nullable = false, length = 30)
    private AreaDireito areaDireito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Tribunal tribunal;

    @Column(nullable = false, length = 100)
    private String vara;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusProcesso status;

    @Column(length = 255)
    private String assunto;

    @Column(name = "data_distribuicao")
    private LocalDate dataDistribuicao;

    @Column(name = "data_arquivamento")
    private LocalDate dataArquivamento;

    @Column(name = "segredo_justica", nullable = false)
    private boolean segredoJustica = false;

    @Column(name = "valor_causa", precision = 15, scale = 2)
    private BigDecimal valorCausa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario responsavel;

    @Column(name = "polo_ativo", columnDefinition = "TEXT")
    private String poloAtivo;

    @Column(name = "polo_passivo", columnDefinition = "TEXT")
    private String poloPassivo;
}