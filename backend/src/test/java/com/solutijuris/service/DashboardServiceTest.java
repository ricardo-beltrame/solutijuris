package com.solutijuris.service;

import com.solutijuris.model.entity.Processo;
import com.solutijuris.model.enums.AreaDireito;
import com.solutijuris.model.enums.StatusProcesso;
import com.solutijuris.repository.PrazoRepository;
import com.solutijuris.repository.ProcessoRepository;
import com.solutijuris.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProcessoRepository processoRepository;
    @Mock
    private PrazoRepository prazoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("obterIndicadores retorna contagens")
    void deveObterIndicadores() {
        when(processoRepository.count()).thenReturn(5L);
        when(usuarioRepository.count()).thenReturn(3L);
        when(prazoRepository.findPrazosVencidos(any())).thenReturn(List.of());
        when(prazoRepository.findByStatus(any())).thenReturn(List.of());
        when(processoRepository.findByStatus(any())).thenReturn(List.of());

        var dto = dashboardService.obterIndicadores();

        assertThat(dto.totalProcessos()).isEqualTo(5L);
        assertThat(dto.totalUsuarios()).isEqualTo(3L);
    }

    @Test
    @DisplayName("obterDistribuicaoPorArea agrupa por area")
    void deveObterDistribuicaoPorArea() {
        Processo civil = new Processo();
        civil.setAreaDireito(AreaDireito.CIVEL);
        Processo trabalhista = new Processo();
        trabalhista.setAreaDireito(AreaDireito.TRABALHISTA);

        when(processoRepository.findByStatus(StatusProcesso.ATIVO))
                .thenReturn(List.of(civil, trabalhista, civil));

        Map<String, Long> distribuicao = dashboardService.obterDistribuicaoPorArea();

        assertThat(distribuicao.get("CIVEL")).isEqualTo(2L);
        assertThat(distribuicao.get("TRABALHISTA")).isEqualTo(1L);
    }
}