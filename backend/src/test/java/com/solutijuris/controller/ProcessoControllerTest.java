package com.solutijuris.controller;

import com.solutijuris.exception.ProcessoNaoEncontradoException;
import com.solutijuris.model.entity.Processo;
import com.solutijuris.model.enums.AreaDireito;
import com.solutijuris.model.enums.StatusProcesso;
import com.solutijuris.model.enums.Tribunal;
import com.solutijuris.security.JwtProvider;
import com.solutijuris.service.ProcessoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.solutijuris.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(
        controllers = ProcessoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "ricardo@solutijuris.com")
class ProcessoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessoService processoService;

    @MockitoBean
    private JwtProvider jwtProvider;

    private Processo processoValido() {
        Processo p = new Processo();
        p.setId(UUID.randomUUID());
        p.setNumeroUnico("1234567-30.2024.8.26.0100");
        p.setAreaDireito(AreaDireito.CIVEL);
        p.setTribunal(Tribunal.TJ_SP);
        p.setStatus(StatusProcesso.ATIVO);
        p.setVara("2ª Vara Cível");
        return p;
    }

    @Test
    @DisplayName("POST /processos retorna 201 e o processo criado")
    void deveCadastrarProcesso() throws Exception {
        when(processoService.cadastrar(any(), any())).thenReturn(processoValido());

        String json = """
            {
              "numeroUnico": "1234567-30.2024.8.26.0100",
              "areaDireito": "CIVEL",
              "tribunal": "TJ_SP",
              "vara": "2ª Vara Cível",
              "assunto": "Indenização",
              "dataDistribuicao": "2024-05-10",
              "segredoJustica": false,
              "valorCausa": 15000.00,
              "responsavelId": "%s",
              "poloAtivo": "Autor",
              "poloPassivo": "Réu"
            }
            """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/processos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroUnico").value("1234567-30.2024.8.26.0100"));

    }

    @Test
    @DisplayName("GET /processos retorna lista")
    void deveListarTodos() throws Exception {
        when(processoService.listarTodos()).thenReturn(List.of(processoValido()));

        mockMvc.perform(get("/processos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /processos/{id} retorna o processo")
    void deveBuscarPorId() throws Exception {
        when(processoService.buscarPorId(any())).thenReturn(processoValido());

        mockMvc.perform(get("/processos/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroUnico").value("1234567-30.2024.8.26.0100"));
    }

    @Test
    @DisplayName("GET /processos/{id} retorna 404 quando não existe")
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        when(processoService.buscarPorId(any()))
                .thenThrow(new ProcessoNaoEncontradoException("Processo não encontrado"));

        mockMvc.perform(get("/processos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /processos/numero/{numero} retorna o processo")
    void deveBuscarPorNumero() throws Exception {
        when(processoService.buscarPorNumeroUnico(any())).thenReturn(processoValido());

        mockMvc.perform(get("/processos/numero/{numero}", "1234567-30.2024.8.26.0100"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /processos/responsavel/{id} retorna lista")
    void deveListarPorResponsavel() throws Exception {
        when(processoService.listarPorResponsavel(any())).thenReturn(List.of(processoValido()));

        mockMvc.perform(get("/processos/responsavel/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}