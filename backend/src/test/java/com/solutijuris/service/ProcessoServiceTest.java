package com.solutijuris.service;

import com.solutijuris.dto.ProcessoRequest;
import com.solutijuris.model.entity.Processo;
import com.solutijuris.model.entity.Usuario;
import com.solutijuris.model.enums.AreaDireito;
import com.solutijuris.model.enums.PerfilUsuario;
import com.solutijuris.model.enums.StatusProcesso;
import com.solutijuris.model.enums.Tribunal;
import com.solutijuris.repository.LogAuditoriaRepository;
import com.solutijuris.repository.ProcessoRepository;
import com.solutijuris.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessoServiceTest {

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @Mock
    private CnjValidator cnjValidator;

    private ProcessoService processoService;

    @BeforeEach
    void setUp() {
        processoService = new ProcessoService(
                processoRepository,
                usuarioRepository,
                logAuditoriaRepository,
                cnjValidator
        );
    }

    @Test
    @DisplayName("Deve cadastrar processo com sucesso")
    void deveCadastrarProcesso() {
        var responsavelId = UUID.randomUUID();
        var request = new ProcessoRequest(
                "1234567-88.2024.8.26.0100",
                AreaDireito.CIVEL,
                Tribunal.TJ_SP,
                "1ª Vara Cível",
                "Indenização",
                LocalDate.of(2024, 3, 15),
                false,
                new BigDecimal(5000.00),
                "João da Silva",
                "Empresa XYZ Ltda"
        );

        var responsavel = new Usuario();
        responsavel.setId(responsavelId);
        responsavel.setEmail("admin@solutijuris.com");

        when(cnjValidator.validar(request.numeroUnico())).thenReturn(true);
        when(processoRepository.existsByNumeroUnico(request.numeroUnico())).thenReturn(false);
        when(usuarioRepository.findByEmail("admin@solutijuris.com")).thenReturn(Optional.of(responsavel));
        when(processoRepository.save(any(Processo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var processo = processoService.cadastrar(request, "admin@solutijuris.com");

        assertNotNull(processo);
        assertEquals(request.numeroUnico(), processo.getNumeroUnico());
        assertEquals(AreaDireito.CIVEL, processo.getAreaDireito());
        assertEquals(Tribunal.TJ_SP, processo.getTribunal());
        assertEquals(StatusProcesso.ATIVO, processo.getStatus());
        assertEquals(responsavelId, processo.getResponsavel().getId());
    }

    @Test
    @DisplayName("Deve rejeitar número CNJ inválido")
    void deveRejeitarCnjInvalido() {
        var request = new ProcessoRequest(
                "1234567-89.2024.8.26.0100",
                AreaDireito.CIVEL,
                Tribunal.TJ_SP,
                "1ª Vara",
                "Teste",
                LocalDate.now(),
                false,
                null,
                "Autor",
                "Réu"
        );

        when(cnjValidator.validar(request.numeroUnico())).thenReturn(false);

        var exception = assertThrows(RuntimeException.class,
                () -> processoService.cadastrar(request, "admin@teste.com"));

        assertTrue(exception.getMessage().contains("CNJ"));
    }

    @Test
    @DisplayName("Deve rejeitar número CNJ duplicado")
    void deveRejeitarCnjDuplicado() {
        var request = new ProcessoRequest(
                "1234567-88.2024.8.26.0100",
                AreaDireito.CIVEL,
                Tribunal.TJ_SP,
                "1ª Vara",
                "Teste",
                LocalDate.now(),
                false,
                null,
                "Autor",
                "Réu"
        );

        when(cnjValidator.validar(request.numeroUnico())).thenReturn(true);
        when(processoRepository.existsByNumeroUnico(request.numeroUnico())).thenReturn(true);

        var exception = assertThrows(RuntimeException.class,
                () -> processoService.cadastrar(request, "admin@teste.com"));

        assertTrue(exception.getMessage().contains("já cadastrado"));
    }
}