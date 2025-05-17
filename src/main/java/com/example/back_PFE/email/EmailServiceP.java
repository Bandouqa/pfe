package com.example.back_PFE.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceP {
    private final JavaMailSender mailSender;

    public EmailServiceP(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendApprovalEmail(String toEmail, String partenaireName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String emailContent = """
                <p>Hello, <b></b>%s</p>
                <p>Thank you for submitting your partnership proposal to China Study Company. 
                We acknowledge receipt of your submission and appreciate your interest in collaborating with us.<br>
                Our team will carefully review your proposal. We will be in touch with you in the near future to discuss it further or to inform you of our decision regarding potential next steps<br>
                In the meantime, please ensure that you regularly check your email account, including your spam , for any updates or communication from our team.<br>
                Thank you for your patience and understanding. We look forward to potentially exploring this opportunity with you.</p>
                <p>Sincerely.</p>
                """.formatted(partenaireName);

            helper.setTo(toEmail);
            helper.setSubject("Partnership Proposal Received");
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("E-mail envoyé avec succès à " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail à " + toEmail);
            e.printStackTrace();
        }
    }
}

