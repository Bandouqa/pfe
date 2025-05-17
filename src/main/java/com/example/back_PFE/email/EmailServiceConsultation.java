package com.example.back_PFE.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceConsultation {
    private final JavaMailSender mailSender;

    public EmailServiceConsultation(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendApprovalEmail(String toEmail, String partenaireName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String emailContent = """
                <p>Hello, <b></b>%s</p>
                <p>This email confirms your reservation for a coaching session with us, China Study Company.\s
                       We are looking forward to our meeting.<br>
                       To secure your scheduled appointment, we kindly request that you confirm your presence within the next 48 hours.
                       Failure to confirm within this timeframe will unfortunately result in the automatic cancellation of your reservation.
                       This policy ensures that all available slots can be offered to other clients.</p>
                <p>Best regards</p>
                """.formatted(partenaireName);

            helper.setTo(toEmail);
            helper.setSubject("Confirmation Required: Your Coaching Session Reservation");
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("E-mail envoyé avec succès à " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail à " + toEmail);
            e.printStackTrace();
        }
    }
}

