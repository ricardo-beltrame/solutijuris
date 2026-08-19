package com.solutijuris.controller;

import com.solutijuris.security.JwtAuthenticationFilter;
import com.solutijuris.security.JwtProvider;
import com.solutijuris.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = DashboardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("GET /dashboard retorna indicadores")
    void deveObterIndicadores() throws Exception {
        when(dashboardService.obterIndicadores()).thenReturn(
                new DashboardService.DashboardDTO(1, 1, 0, 1, 0, 1, 0));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessos").value(1));
    }

    @Test
    @DisplayName("GET /dashboard/distribuicao retorna mapa")
    void deveObterDistribuicao() throws Exception {
        when(dashboardService.obterDistribuicaoPorArea())
                .thenReturn(Map.of("CIVEL", 2L));

        mockMvc.perform(get("/dashboard/distribuicao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CIVEL").value(2));
    }
}