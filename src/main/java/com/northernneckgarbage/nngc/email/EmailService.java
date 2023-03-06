package com.northernneckgarbage.nngc.email;

import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.stripe.Stripe;
import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.sendgrid.*;
import io.github.cdimascio.dotenv.Dotenv;




@Service
public class EmailService  implements EmailSender {

   Dotenv dotenv = Dotenv.load();




    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);


    @Override
    public void sendWithSendGrid(String to, String subject, Content content) throws java.io.IOException {
        Email from = new Email("bishop@northernneckgarbage.com");
Email emailTo = new Email(to);
      Mail mail = new Mail(from, subject, emailTo, content);
        SendGrid sendGrid = new SendGrid(dotenv.get("SENDGRID_API_KEY"));

        Request request = new Request();
LOGGER.info("Sending email to " + to);
        try {
          request.setMethod(Method.POST);
          request.setEndpoint("mail/send");
          request.setBody(mail.build());
          Response response = sendGrid.api(request);
            LOGGER.warn("Sending email to " +response.getStatusCode());
            LOGGER.info("Response Body " +response.getBody());
            LOGGER.info("Response Headers " +response.getHeaders());
        } catch (java.io.IOException ex) {
          throw ex;
        }
    }


}
