package com.solutijuris.controller;

import com.solutijuris.dto.PrazoRequest;
import com.solutijuris.model.entity.Prazo;
import com.solutijuris.service.PrazoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prazos")
@RequiredArgsConstructor
public class PrazoController {

    private final PrazoService prazoService;

    @PostMapping
    public ResponseEntity<Prazo> cadastrar(
            @Valid @RequestBody PrazoRequest request,
            Authentication authentication) {
        var prazo = prazoService.cadastrar(
                request.processoId(),
                request.responsavelId(),
                request.dataVencimento(),
                request.descricao(),
                authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(prazo);
    }

    @PatchMapping("/{id}/cumprir")
    public ResponseEntity<Prazo> cumprir(
            @PathVariable UUID id,
            Authentication authentication) {
        return ResponseEntity.ok(prazoService.cumprir(id, authentication.getName()));
    }

    @GetMapping("/processo/{processoId}")
    public ResponseEntity<List<Prazo>> listarPorProcesso(
            @PathVariable UUID processoId) {
        return ResponseEntity.ok(prazoService.listarPorProcesso(processoId));
    }

    @GetMapping("/responsavel/{responsavelId}")
    public ResponseEntity<List<Prazo>> listarPorResponsavel(
            @PathVariable UUID responsavelId) {
        return ResponseEntity.ok(prazoService.listarPorResponsavel(responsavelId));
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<Prazo>> listarVencidos() {
        return ResponseEntity.ok(prazoService.listarVencidos());
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Prazo>> listarPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        return ResponseEntity.ok(prazoService.listarPorPeriodo(inicio, fim));
    }
}