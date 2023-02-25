package com.northernneckgarbage.nngc.email;

import com.northernneckgarbage.nngc.repository.CustomerRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    //String link = " http://localhost:8080/auth/nngc/authenticate?token=" + token;
    @Override
    public void send(String to, String subject) {

        LOGGER.info("Sending email to " + to);

        try{
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, "UTF-8");
            helper.setText(subject, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("info@northernneckgarbage.com");
            mailSender.send(message);

    } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
