package com.solutijuris.service;

import com.solutijuris.model.entity.Prazo;
import com.solutijuris.model.entity.Processo;
import com.solutijuris.model.entity.Usuario;
import com.solutijuris.model.enums.StatusPrazo;
import com.solutijuris.repository.LogAuditoriaRepository;
import com.solutijuris.repository.PrazoRepository;
import com.solutijuris.repository.ProcessoRepository;
import com.solutijuris.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrazoServiceTest {

    @Mock
    private PrazoRepository prazoRepository;
    @Mock
    private ProcessoRepository processoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @InjectMocks
    private PrazoService prazoService;

    private Processo processoValido() {
        Processo p = new Processo();
        p.setId(UUID.randomUUID());
        p.setNumeroUnico("1234567-89.2024.8.26.0100");
        return p;
    }

    private Usuario usuarioValido() {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setEmail("advogado@solutijuris.com");
        return u;
    }

    @Test
    @DisplayName("cadastrar cria prazo e log de auditoria")
    void deveCadastrarPrazo() {
        when(processoRepository.findById(any())).thenReturn(Optional.of(processoValido()));
        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuarioValido()));
        when(prazoRepository.save(any())).thenAnswer(inv -> {
            Prazo p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        Prazo result = prazoService.cadastrar(
                UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now().plusDays(5), "Contestar",
                "advogado@solutijuris.com");

        assertThat(result.getStatus()).isEqualTo(StatusPrazo.ABERTO);
        verify(prazoRepository).save(any());
        verify(logAuditoriaRepository).save(any());
    }

    @Test
    @DisplayName("cumprir marca prazo como cumprido")
    void deveCumprirPrazo() {
        Prazo prazo = new Prazo();
        prazo.setId(UUID.randomUUID());
        when(prazoRepository.findById(any())).thenReturn(Optional.of(prazo));

        Prazo result = prazoService.cumprir(prazo.getId(), "advogado@solutijuris.com");

        assertThat(result.getStatus()).isEqualTo(StatusPrazo.CUMPRIDO);
        assertThat(result.getDataCumprimento()).isNotNull();
        verify(logAuditoriaRepository).save(any());
    }

    @Test
    @DisplayName("listarPorProcesso retorna lista")
    void deveListarPorProcesso() {
        when(prazoRepository.findByProcessoId(any())).thenReturn(List.of(new Prazo()));

        assertThat(prazoService.listarPorProcesso(UUID.randomUUID())).hasSize(1);
    }

    @Test
    @DisplayName("listarPorResponsavel retorna lista")
    void deveListarPorResponsavel() {
        when(prazoRepository.findByResponsavelId(any())).thenReturn(List.of(new Prazo()));

        assertThat(prazoService.listarPorResponsavel(UUID.randomUUID())).hasSize(1);
    }

    @Test
    @DisplayName("listarVencidos retorna lista")
    void deveListarVencidos() {
        when(prazoRepository.findPrazosVencidos(any())).thenReturn(List.of(new Prazo()));

        assertThat(prazoService.listarVencidos()).hasSize(1);
    }

    @Test
    @DisplayName("listarPorPeriodo retorna lista")
    void deveListarPorPeriodo() {
        when(prazoRepository.findByDataVencimentoBetween(any(), any()))
                .thenReturn(List.of(new Prazo()));

        assertThat(prazoService.listarPorPeriodo(
                LocalDate.now(), LocalDate.now().plusDays(10))).hasSize(1);
    }

    @Test
    @DisplayName("atualizarPrazosVencidos marca como vencido")
    void deveAtualizarPrazosVencidos() {
        Prazo prazo = new Prazo();
        prazo.setId(UUID.randomUUID());
        when(prazoRepository.findPrazosVencidos(any())).thenReturn(List.of(prazo));

        prazoService.atualizarPrazosVencidos();

        assertThat(prazo.getStatus()).isEqualTo(StatusPrazo.VENCIDO);
        verify(prazoRepository).saveAll(any());
    }
}