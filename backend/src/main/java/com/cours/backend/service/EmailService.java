package com.cours.backend.service;

import com.cours.backend.model.TypeCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String expediteur;

    public EmailService(JavaMailSender mailSender, @Value("${app.mail.expediteur}") String expediteur) {
        this.mailSender = mailSender;
        this.expediteur = expediteur;
    }

    public void envoyerCode(String destinataire, String prenom, String code, TypeCode type) {
        boolean inscription = type == TypeCode.INSCRIPTION;
        String sujet = inscription ? "Confirme ton inscription sur Cours+" : "Reinitialisation de ton mot de passe Cours+";
        String intro = inscription
                ? "Encore une etape et ton compte est pret : saisis ce code sur la page d'inscription."
                : "Voici le code pour choisir un nouveau mot de passe.";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(expediteur, "Cours+");
            helper.setTo(destinataire);
            helper.setSubject(sujet);
            helper.setText(construireHtml(prenom, intro, code), true);
            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("Echec de l'envoi de l'e-mail : " + e.getMessage(), e);
        }
    }

    private String construireHtml(String prenom, String intro, String code) {
        return """
                <!doctype html>
                <html lang="fr">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Cours+</title>
                </head>
                <body style="margin:0; padding:0; background:#f3e9d7; font-family:Arial, Helvetica, sans-serif;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f3e9d7; padding:32px 16px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:480px; background:#fbf4e8; border-radius:18px; overflow:hidden; border:2px solid #1f1730;">
                          <tr>
                            <td style="background:#4a2e83; padding:24px; text-align:center;">
                              <span style="font-size:22px; font-weight:bold; color:#ffffff;">Cours<span style="color:#ff6a3d;">+</span></span>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px 28px;">
                              <p style="margin:0 0 4px; font-size:17px; color:#1f1730;">Bonjour %s,</p>
                              <p style="margin:0 0 24px; font-size:15px; color:#4a4458; line-height:1.5;">%s</p>
                              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td align="center" style="background:#ffffff; border:2.5px solid #1f1730; border-radius:12px; padding:18px;">
                                    <span style="font-size:34px; font-weight:bold; letter-spacing:10px; color:#ff6a3d; font-family:'Courier New', monospace;">%s</span>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:20px 0 0; font-size:13px; color:#8a8296;">Ce code expire dans 15 minutes. Si tu n'es pas a l'origine de cette demande, ignore cet e-mail.</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#4a2e83; padding:18px 24px; text-align:center;">
                              <p style="margin:0; font-size:11px; color:rgba(255,255,255,0.75);">Cours+ - Diomande Keuwe Mickael - Projet academique</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(prenom, intro, code);
    }
}
