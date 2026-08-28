package com.solutijuris.service;

import com.solutijuris.model.entity.LogAuditoria;
import com.solutijuris.model.entity.Prazo;
import com.solutijuris.model.entity.Processo;
import com.solutijuris.model.entity.Usuario;
import com.solutijuris.model.enums.StatusPrazo;
import com.solutijuris.repository.LogAuditoriaRepository;
import com.solutijuris.repository.PrazoRepository;
import com.solutijuris.repository.ProcessoRepository;
import com.solutijuris.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrazoService {

    private final PrazoRepository prazoRepository;
    private final ProcessoRepository processoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;

    @Transactional
    public Prazo cadastrar(UUID processoId, UUID responsavelId,
                           LocalDate dataVencimento, String descricao,
                           String emailUsuario) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));

        Usuario responsavel = usuarioRepository.findById(responsavelId)
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado"));

        var prazo = new Prazo();
        prazo.setProcesso(processo);
        prazo.setResponsavel(responsavel);
        prazo.setDataVencimento(dataVencimento);
        prazo.setDescricao(descricao);
        prazo.setStatus(StatusPrazo.ABERTO);

        prazoRepository.save(prazo);

        var log = new LogAuditoria();
        log.setUsuarioEmail(emailUsuario);
        log.setAcao("CADASTRO_PRAZO");
        log.setEntidade("Prazo");
        log.setEntidadeId(prazo.getId());
        log.setDadosNovos("Processo: " + processo.getNumeroUnico()
                + ", Vencimento: " + dataVencimento
                + ", Descrição: " + descricao);
        logAuditoriaRepository.save(log);

        return prazo;
    }

    @Transactional
    public Prazo cumprir(UUID id, String emailUsuario) {
        Prazo prazo = prazoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prazo não encontrado"));

        prazo.setStatus(StatusPrazo.CUMPRIDO);
        prazo.setDataCumprimento(LocalDate.now());

        prazoRepository.save(prazo);

        var log = new LogAuditoria();
        log.setUsuarioEmail(emailUsuario);
        log.setAcao("CUMPRIR_PRAZO");
        log.setEntidade("Prazo");
        log.setEntidadeId(prazo.getId());
        logAuditoriaRepository.save(log);

        return prazo;
    }

    public List<Prazo> listarPorProcesso(UUID processoId) {
        return prazoRepository.findByProcessoId(processoId);
    }

    public List<Prazo> listarPorResponsavel(UUID responsavelId) {
        return prazoRepository.findByResponsavelId(responsavelId);
    }

    public List<Prazo> listarVencidos() {
        return prazoRepository.findPrazosVencidos(LocalDate.now());
    }

    public List<Prazo> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return prazoRepository.findByDataVencimentoBetween(inicio, fim);
    }

    @Transactional
    public void atualizarPrazosVencidos() {
        List<Prazo> vencidos = prazoRepository
                .findPrazosVencidos(LocalDate.now());

        for (Prazo prazo : vencidos) {
            prazo.setStatus(StatusPrazo.VENCIDO);
        }

        prazoRepository.saveAll(vencidos);
    }

    public List<Prazo> listarTodos() {
        return prazoRepository.findAll();
    }
}