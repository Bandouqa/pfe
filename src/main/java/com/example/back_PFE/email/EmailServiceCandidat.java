package com.example.back_PFE.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceCandidat {
    private final JavaMailSender mailSender;

    public EmailServiceCandidat(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendApprovalEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String emailContent = """
                <p>Hello <b>
                <p>We are China Study Company, we confirm that we have successfully received your application.
                 Our team is currently reviewing all submissions, and we will be in touch with you regarding the next steps in the selection process in due course.
                 Please ensure that you regularly check your email account, including your spam or junk folder, for any updates or further communication from ust
                .</p>
                <p>Thank you for your patience</p>
                """;

            helper.setTo(toEmail);
            helper.setSubject("Application Received");
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("E-mail envoyé avec succès à " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail à " + toEmail);
            e.printStackTrace();
        }
    }
}
