package com.solutijuris.service;

import com.solutijuris.model.enums.AreaDireito;
import com.solutijuris.model.enums.StatusPrazo;
import com.solutijuris.model.enums.StatusProcesso;
import com.solutijuris.repository.PrazoRepository;
import com.solutijuris.repository.ProcessoRepository;
import com.solutijuris.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProcessoRepository processoRepository;
    private final PrazoRepository prazoRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardDTO obterIndicadores() {
        long totalProcessos = processoRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long prazosVencidos = prazoRepository.findPrazosVencidos(LocalDate.now()).size();
        long prazosAbertos = prazoRepository.findByStatus(StatusPrazo.ABERTO).size();
        long prazosCumpridos = prazoRepository.findByStatus(StatusPrazo.CUMPRIDO).size();
        long processosAtivos = processoRepository.findByStatus(StatusProcesso.ATIVO).size();
        long processosArquivados = processoRepository.findByStatus(StatusProcesso.ARQUIVADO).size();

        return new DashboardDTO(
                totalProcessos,
                totalUsuarios,
                prazosVencidos,
                prazosAbertos,
                prazosCumpridos,
                processosAtivos,
                processosArquivados
        );
    }

    public Map<String, Long> obterDistribuicaoPorArea() {
        Map<String, Long> distribuicao = new LinkedHashMap<>();
        for (AreaDireito area : AreaDireito.values()) {
            long count = processoRepository.findByStatus(StatusProcesso.ATIVO)
                    .stream()
                    .filter(p -> p.getAreaDireito() == area)
                    .count();
            if (count > 0) {
                distribuicao.put(area.name(), count);
            }
        }
        return distribuicao;
    }

    public record DashboardDTO(
            long totalProcessos,
            long totalUsuarios,
            long prazosVencidos,
            long prazosAbertos,
            long prazosCumpridos,
            long processosAtivos,
            long processosArquivados
    ) {}
}