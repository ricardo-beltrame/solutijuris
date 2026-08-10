package com.solutijuris.controller;

import com.solutijuris.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardService.DashboardDTO> indicadores() {
        return ResponseEntity.ok(dashboardService.obterIndicadores());
    }

    @GetMapping("/distribuicao")
    public ResponseEntity<Map<String, Long>> distribuicaoPorArea() {
        return ResponseEntity.ok(dashboardService.obterDistribuicaoPorArea());
    }
}