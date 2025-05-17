package com.example.back_PFE.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendApprovalEmail(String toEmail, String clientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String emailContent = """
                <h1>Votre demande a été acceptée</h1>
                <p>Bonjour <b></b>%s</p>
                <p>Nous sommes ravis de vous informer que votre demande a été acceptée.</p>
                <p>Cordialement,<br>L'équipe.</p>
                """.formatted(clientName);

            helper.setTo(toEmail);
            helper.setSubject("Confirmation d'acceptation de votre demande");
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("E-mail envoyé avec succès à " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail à " + toEmail);
            e.printStackTrace();
        }
    }
}

