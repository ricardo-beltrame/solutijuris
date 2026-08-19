package com.solutijuris.controller;

import com.solutijuris.model.entity.Prazo;
import com.solutijuris.model.enums.StatusPrazo;
import com.solutijuris.security.JwtAuthenticationFilter;
import com.solutijuris.security.JwtProvider;
import com.solutijuris.service.PrazoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PrazoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "advogado@solutijuris.com")
class PrazoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrazoService prazoService;

    @MockitoBean
    private JwtProvider jwtProvider;

    private Prazo prazoValido() {
        Prazo p = new Prazo();
        p.setId(UUID.randomUUID());
        p.setDataVencimento(LocalDate.now().plusDays(5));
        p.setDescricao("Contestar");
        p.setStatus(StatusPrazo.ABERTO);
        return p;
    }

    @Test
    @DisplayName("POST /prazos retorna 201")
    void deveCadastrarPrazo() throws Exception {
        when(prazoService.cadastrar(any(), any(), any(), any(), any()))
                .thenReturn(prazoValido());

        String json = """
            {
              "processoId": "%s",
              "responsavelId": "%s",
              "dataVencimento": "2026-09-01",
              "descricao": "Contestar"
            }
            """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/prazos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Contestar"));
    }

    @Test
    @DisplayName("PATCH /prazos/{id}/cumprir retorna 200")
    void deveCumprirPrazo() throws Exception {
        when(prazoService.cumprir(any(), any())).thenReturn(prazoValido());

        mockMvc.perform(patch("/prazos/{id}/cumprir", UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /prazos/processo/{id} retorna lista")
    void deveListarPorProcesso() throws Exception {
        when(prazoService.listarPorProcesso(any())).thenReturn(List.of(prazoValido()));

        mockMvc.perform(get("/prazos/processo/{id}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /prazos/vencidos retorna lista")
    void deveListarVencidos() throws Exception {
        when(prazoService.listarVencidos()).thenReturn(List.of(prazoValido()));

        mockMvc.perform(get("/prazos/vencidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /prazos/periodo retorna lista")
    void deveListarPorPeriodo() throws Exception {
        when(prazoService.listarPorPeriodo(any(), any()))
                .thenReturn(List.of(prazoValido()));

        mockMvc.perform(get("/prazos/periodo")
                        .param("inicio", "2026-08-01")
                        .param("fim", "2026-09-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}