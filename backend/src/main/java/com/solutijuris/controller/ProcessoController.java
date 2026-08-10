package com.solutijuris.controller;

import com.solutijuris.dto.ProcessoRequest;
import com.solutijuris.model.entity.Processo;
import com.solutijuris.service.ProcessoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/processos")
@RequiredArgsConstructor
public class ProcessoController {

    private final ProcessoService processoService;

    @PostMapping
    public ResponseEntity<Processo> cadastrar(
            @Valid @RequestBody ProcessoRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        var processo = processoService.cadastrar(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(processo);
    }

    @GetMapping
    public ResponseEntity<List<Processo>> listarTodos() {
        return ResponseEntity.ok(processoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Processo> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(processoService.buscarPorId(id));
    }

    @GetMapping("/numero/{numeroUnico}")
    public ResponseEntity<Processo> buscarPorNumero(
            @PathVariable String numeroUnico) {
        return ResponseEntity.ok(processoService.buscarPorNumeroUnico(numeroUnico));
    }

    @GetMapping("/responsavel/{responsavelId}")
    public ResponseEntity<List<Processo>> listarPorResponsavel(
            @PathVariable UUID responsavelId) {
        return ResponseEntity.ok(processoService.listarPorResponsavel(responsavelId));
    }
}