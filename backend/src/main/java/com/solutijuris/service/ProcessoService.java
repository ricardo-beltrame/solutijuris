package com.solutijuris.service;

import com.solutijuris.dto.ProcessoRequest;
import com.solutijuris.exception.ProcessoInvalidoException;
import com.solutijuris.exception.ProcessoNaoEncontradoException;
import com.solutijuris.exception.ResponsavelNaoEncontradoException;
import com.solutijuris.model.entity.LogAuditoria;
import com.solutijuris.model.entity.Processo;
import com.solutijuris.model.entity.Usuario;
import com.solutijuris.model.enums.StatusProcesso;
import com.solutijuris.repository.LogAuditoriaRepository;
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
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final CnjValidator cnjValidator;

    @Transactional
    public Processo cadastrar(ProcessoRequest request, String emailUsuario) {
        if (!cnjValidator.validar(request.numeroUnico())) {
            throw new ProcessoInvalidoException(
                    "Número único CNJ inválido — dígito verificador não confere");
        }

        if (processoRepository.existsByNumeroUnico(request.numeroUnico())) {
            throw new ProcessoInvalidoException("Número único já cadastrado");
        }

        Usuario responsavel = usuarioRepository.findById(request.responsavelId())
                .orElseThrow(() -> new ResponsavelNaoEncontradoException("Responsável não encontrado"));

        var processo = new Processo();
        processo.setNumeroUnico(request.numeroUnico());
        processo.setAreaDireito(request.areaDireito());
        processo.setTribunal(request.tribunal());
        processo.setVara(request.vara());
        processo.setStatus(StatusProcesso.ATIVO);
        processo.setAssunto(request.assunto());
        processo.setDataDistribuicao(request.dataDistribuicao() != null
                ? request.dataDistribuicao() : LocalDate.now());
        processo.setSegredoJustica(request.segredoJustica());
        processo.setValorCausa(request.valorCausa());
        processo.setResponsavel(responsavel);
        processo.setPoloAtivo(request.poloAtivo());
        processo.setPoloPassivo(request.poloPassivo());
        processoRepository.save(processo);

        var log = new LogAuditoria();
        log.setUsuarioEmail(emailUsuario);
        log.setAcao("CADASTRO_PROCESSO");
        log.setEntidade("Processo");
        log.setEntidadeId(processo.getId());
        log.setDadosNovos("Número: " + request.numeroUnico()
                + ", Área: " + request.areaDireito()
                + ", Tribunal: " + request.tribunal());
        logAuditoriaRepository.save(log);

        return processo;
    }

    @Transactional(readOnly = true)
    public List<Processo> listarTodos() {
        return processoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Processo buscarPorId(UUID id) {
        return processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNaoEncontradoException("Processo não encontrado"));
    }

    @Transactional(readOnly = true)
    public Processo buscarPorNumeroUnico(String numeroUnico) {
        return processoRepository.findByNumeroUnico(numeroUnico)
                .orElseThrow(() -> new ProcessoNaoEncontradoException("Processo não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Processo> listarPorResponsavel(UUID responsavelId) {
        return processoRepository.findByResponsavelId(responsavelId);
    }
}