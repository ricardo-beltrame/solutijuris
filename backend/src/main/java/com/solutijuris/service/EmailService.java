package com.solutijuris.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacao(String destinatario, String token) {
        String link = frontendUrl + "/redefinir-senha?token=" + token;
        String assunto = "SolutioJuris - Recuperação de Senha";
        String corpo = """
            <div style="font-family: Inter, Arial, sans-serif; max-width: 480px; margin: 0 auto;">
                <h2 style="color: #1976D2;">Recuperação de Senha</h2>
                <p style="color: #424242; font-size: 15px;">Recebemos uma solicitação para redefinir sua senha no SolutioJuris.</p>
                <p style="color: #424242; font-size: 15px;">Clique no botão abaixo para criar uma nova senha:</p>
                <a href="%s" style="display: inline-block; background: #1976D2; color: #FFFFFF; padding: 12px 32px; border-radius: 8px; text-decoration: none; font-weight: 600; margin: 16px 0;">Redefinir Senha</a>
                <p style="color: #9E9E9E; font-size: 13px;">Este link expira em 1 hora.</p>
                <p style="color: #9E9E9E; font-size: 13px;">Se você não solicitou esta alteração, ignore este e-mail.</p>
                <hr style="border: none; border-top: 1px solid #E0E0E0; margin: 24px 0;">
                <p style="color: #BDBDBD; font-size: 12px;">© 2026 SolutioJuris - Sistema de Gestão Jurídica</p>
            </div>
            """.formatted(link);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpo, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            // Loga o erro mas não quebra o fluxo — o token foi criado
            System.err.println("[EMAIL] Erro ao enviar: " + e.getMessage());
        }

        // Log do link para desenvolvimento (testar sem SMTP configurado)
        System.out.println("[RECUPERACAO SENHA] Link de redefinição: " + link);
    }
}