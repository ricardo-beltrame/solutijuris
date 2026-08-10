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

        // Estrutura: NNNNNNN-DD.AAAA.J.TR.OOOO
        // NNNNNNN (0-6) | DD (7-8) | AAAA (9-12) | J (13) | TR (14-15) | OOOO (16-19)
        String base = apenasDigitos.substring(0, 7)   // NNNNNNN
                + apenasDigitos.substring(9, 20);     // AAAA + J + TR + OOOO (18 dígitos)
        String digitosInformados = apenasDigitos.substring(7, 9); // DD

        return digitosInformados.equals(calcularDigitosVerificadores(base));
    }

    private String calcularDigitosVerificadores(String base) {
        int d1 = calcularDigito(base);
        int d2 = calcularDigito(base + d1);
        return String.format("%d%d", d1, d2);
    }

    private int calcularDigito(String numero) {
        int soma = 0;
        int peso = 2;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            soma += digito * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }
        int resto = soma % 11;
        return (resto == 0 || resto == 1) ? 0 : 11 - resto;
    }
}