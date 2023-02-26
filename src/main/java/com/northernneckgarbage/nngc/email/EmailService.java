package com.northernneckgarbage.nngc.email;

import com.northernneckgarbage.nngc.repository.CustomerRepository;
import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.sendgrid.*;

@AllArgsConstructor
@Service
public class EmailService  implements EmailSender {

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

    @Override
    public void sendWithSendGrid(String to, String subject, Content content) throws java.io.IOException {
        Email from = new Email("bishop@northernneckgarbage.com");
Email emailTo = new Email(to);
      Mail mail = new Mail(from, subject, emailTo, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();

        try {
          request.setMethod(Method.POST);
          request.setEndpoint("mail/send");
          request.setBody(mail.build());
          Response response = sg.api(request);
          System.out.println(response.getStatusCode());
          System.out.println(response.getBody());
          System.out.println(response.getHeaders());
        } catch (java.io.IOException ex) {
          throw ex;
        }
    }


}
