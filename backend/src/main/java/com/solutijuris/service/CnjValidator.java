package com.solutijuris.service;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CnjValidator {

    private static final Pattern FORMATO_CNJ = Pattern.compile(
            "[0-9]{7}-[0-9]{2}[.][0-9]{4}[.][0-9][.][0-9]{2}[.][0-9]{4}");

    public boolean validar(String numeroUnico) {
        if (numeroUnico == null || !FORMATO_CNJ.matcher(numeroUnico).matches()) {
            return false;
        }

        String apenasDigitos = numeroUnico.replaceAll("[^0-9]", "");

        // NNNNNNN-DD.AAAA.J.TR.OOOO → 20 dígitos
        String n    = apenasDigitos.substring(0, 7);    // NNNNNNN
        String dd   = apenasDigitos.substring(7, 9);    // DD
        String aaaa = apenasDigitos.substring(9, 13);   // AAAA
        String j    = apenasDigitos.substring(13, 14);  // J
        String tr   = apenasDigitos.substring(14, 16);  // TR
        String oooo = apenasDigitos.substring(16, 20);  // OOOO

        // Algoritmo Módulo 97 Base 10 (ISO 7064:2003) — Resolução CNJ 65/2008
        // Etapa 1: NNNNNNN % 97
        long operacao1 = Long.parseLong(n) % 97;

        // Etapa 2: (operacao1 + AAAA + J + TR) % 97
        long operacao2 = Long.parseLong(operacao1 + aaaa + j + tr) % 97;

        // Etapa 3: (operacao2 + OOOO + DD) % 97 → deve ser 1
        long operacaoFinal = Long.parseLong(operacao2 + oooo + dd) % 97;

        return operacaoFinal == 1;
    }
}